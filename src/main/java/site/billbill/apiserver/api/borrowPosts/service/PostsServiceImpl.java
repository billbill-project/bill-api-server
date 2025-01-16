package site.billbill.apiserver.api.borrowPosts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.borrowPosts.converter.PostsConverter;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.api.push.service.PushService;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.common.enums.chat.ChannelState;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.alarm.AlarmListJpaEntity;
import site.billbill.apiserver.model.alarm.AlarmLogJpaEntity;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.model.post.*;
import site.billbill.apiserver.model.post.embeded.ItemsLikeId;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.model.user.UserLocationJpaEntity;
import site.billbill.apiserver.model.user.UserSearchHistJpaEntity;
import site.billbill.apiserver.repository.alarm.AlarmListRepository;
import site.billbill.apiserver.repository.alarm.AlarmLogRepository;
import site.billbill.apiserver.repository.borrowPosts.*;
import site.billbill.apiserver.repository.chat.ChatRepository;
import site.billbill.apiserver.repository.user.UserLocationReposity;
import site.billbill.apiserver.repository.user.UserRepository;
import site.billbill.apiserver.repository.user.UserSearchHistRepository;
import site.billbill.apiserver.scheduler.TaskScheduler.Manager.ReviewNotificationManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



@org.springframework.stereotype.Service
@RequiredArgsConstructor

@Slf4j
public class PostsServiceImpl implements PostsService {

    private final UserRepository userRepository;
    private final ItemsRepository itemsRepository;
    private final ItemsBorrowRepository itemsBorrowRepository;
    private final ItemsBorrowStatusRepository itemsBorrowStatusRepository;
    private final ItemsCategoryRepository itemsCategoryRepository;
    private final UserSearchHistRepository userSearchHistRepository;
    private final SearchKeywordStatRepository searchKeywordStatRepository;
    private final ItemsReivewRepository itemsReivewRepository;
    private final ChatRepository chatRepository;
    private final BorrowHistRepository borrowHistRepository;
    private final ItemsLocationRepository itemsLocationRepository;
    private final UserLocationReposity userLocationReposity;
    private final ItemsLikeRepository itemsLikeRepository;
    private final AlarmListRepository alarmListRepository;
    private final AlarmLogRepository alarmLogRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final PushService pushService;
    private final ReviewNotificationManager reviewNotificationManager;
    private final ReviewAlertRepository reviewAlertRepository;

    public PostsResponse.UploadResponse uploadPostService(PostsRequest.UploadRequest request, String userId) {
        //먼저 item 생성,
        Optional<UserJpaEntity> isUser = userRepository.findById(userId);
        String postsId = ULIDUtil.generatorULID("BORROW");
        ItemsCategoryJpaEntity category = itemsCategoryRepository.findByName(request.getCategory());
        
        UserJpaEntity user = new UserJpaEntity();
        if (isUser.isPresent()) {
            user = isUser.get();
        }
        if(request.getLocation()==null){
            throw new CustomException(ErrorCode.BadRequest, "위치정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        //Item 생성
        ItemsJpaEntity newItem = PostsConverter.toItem(postsId, request, user, category);
        itemsRepository.save(newItem);
        ItemsJpaEntity item = itemsRepository.findById(postsId).orElse(newItem);
        //BorrowItem 생성
        ItemsBorrowJpaEntity newBorrowItem = PostsConverter.toItemBorrow(item, request);
        itemsBorrowRepository.save(newBorrowItem);
        //대여 불가 기간 생성
        if (request.getNoRental() != null && !request.getNoRental().isEmpty()) {
            List<ItemsBorrowStatusJpaEntity> itemsBorrowStatusList = request.getNoRental().stream()
                    .map(status -> PostsConverter.toItemBorrowStatus(item, "RENTAL_NOT_POSSIBLE", status))
                    .toList();
            itemsBorrowStatusRepository.saveAll(itemsBorrowStatusList);
        }
        //좌표 저장
        Point coordinates = geometryFactory.createPoint(new Coordinate(request.getLocation().getLongitude(), request.getLocation().getLatitude()));
        ItemsLocationJpaEntity itemsLocation = PostsConverter.toItemsLocation(coordinates,request.getLocation(), item);
        itemsLocationRepository.save(itemsLocation);
        return PostsConverter.toUploadResponse(postsId);


    }

    public PostsResponse.ViewAllResultResponse ViewAllPostService(
            String category, int page, Sort.Direction direction, String orderType, String userId,Double latitude,Double longitude) {


        Pageable pageable = createPageable(page, direction, orderType);
        List<PostsResponse.Post> items = findAndConvertItems(category, pageable, null, latitude,longitude);
        return PostsConverter.toViewAllList(items);
    }

    public PostsResponse.ViewPostResponse ViewPostService(String postId, String userId) {
        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        ItemsBorrowJpaEntity borrowItem = itemsBorrowRepository.findById(postId).orElse(null);
        UserJpaEntity user = userRepository.findById(userId).orElse(null);
        ItemsLikeId likeId=new ItemsLikeId(postId,userId);
        ItemsLikeJpaEntity itemsLikeJpa= itemsLikeRepository.findByIdAndDelYn(likeId,false);
        ItemsLocationJpaEntity itemsLocation =itemsLocationRepository.findByItem(item);
        boolean isLike;
        if(itemsLikeJpa==null) {
            isLike = false;
        }else{
            isLike = true;
        }
        if (item == null) {
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        String status = "";
        switch (item.getItemStatus()) {
            case 1:
                status = "상";
                break;
            case 2:
                status = "중상";
                break;
            case 3:
                status = "중";
                break;
            case 4:
                status = "중하";
                break;
            case 5:
                status = "하";
                break;

        }
        return PostsConverter.toViewPost(item, borrowItem, status,isLike,itemsLocation);

    }

    @Transactional
    public String deletePostService(String postId, String userId) {
        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        UserJpaEntity user = userRepository.findById(userId).orElse(null);
        if (item == null) {
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        } else if (!item.getOwner().equals(user)) {
            throw new CustomException(ErrorCode.BadRequest, "해당 게시물 작성자가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        item.setDelYn(true);


        return "Success";
    }

    @Transactional
    public String UpdatePostService(String postId, String userId, PostsRequest.UploadRequest request) {
        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        UserJpaEntity user = userRepository.findById(userId).orElse(null);
        ItemsCategoryJpaEntity category = itemsCategoryRepository.findByName(request.getCategory());
        ItemsBorrowJpaEntity borrowItem = itemsBorrowRepository.findById(postId).orElse(null);
        ItemsLocationJpaEntity itemsLocationJpa = itemsLocationRepository.findByItem(item);
        if (item == null) {
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        } else if (!item.getOwner().equals(user)) {
            throw new CustomException(ErrorCode.BadRequest, "해당 게시물 작성자가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        item.setTitle(request.getTitle());
        item.setImages(request.getImages());
        item.setItemStatus(request.getItemStatus());
        item.setCategory(category);
        item.setContent(request.getContent());
        borrowItem.setDeposit(request.getDeposit());
        borrowItem.setPrice(request.getPrice());
        Point coordinates = geometryFactory.createPoint(new Coordinate(request.getLocation().getLongitude(), request.getLocation().getLatitude()));
        itemsLocationJpa.setLatitude(request.getLocation().getLatitude());
        itemsLocationJpa.setLongitude(request.getLocation().getLongitude());
        itemsLocationJpa.setCoordinates(coordinates);
        itemsLocationJpa.setAddress(request.getLocation().getAddress());
        List<ItemsBorrowStatusJpaEntity> existingStatuses = itemsBorrowStatusRepository.findAllByItemIdAndBorrowStatusCode(postId, "RENTAL_NOT_POSSIBLE");
        itemsBorrowStatusRepository.deleteAll(existingStatuses);
        //대여 불가 날짜 새로 배정, 똑같아도 새로 배정되는 느낌
        if (request.getNoRental() != null && !request.getNoRental().isEmpty()) {
            List<ItemsBorrowStatusJpaEntity> newStatuses = request.getNoRental().stream()
                    .map(status -> PostsConverter.toItemBorrowStatus(item, "RENTAL_NOT_POSSIBLE", status))
                    .toList();
            itemsBorrowStatusRepository.saveAll(newStatuses);

        }
        return "Success";
    }

    @Transactional
    public PostsResponse.ViewAllResultResponse ViewSearchPostService(String userId, String category, int page, Sort.Direction direction, String orderType, String keyword, boolean state,Double latitude,Double longitude) {
        UserLocationJpaEntity userLocation = userLocationReposity.findByUserId(userId).orElse(null);

        Pageable pageable = createPageable(page, direction, orderType);
        List<PostsResponse.Post> items = findAndConvertItems(category, pageable, keyword, latitude,longitude);
        //사용자가 검색어 저장을 허용했을 경우
        String tempKeyword = keyword.replaceAll("\\+", " ");
//        if(state){
//
//            UserSearchHistJpaEntity userSearchHist= PostsConverter.toUserSearch(user,tempKeyword);
//            userSearchHistRepository.save(userSearchHist);
//        }
        //추천 검색어를 위해 검색어 를 저장
        SearchKeywordStatsJpaEntity searchKeywordStats = searchKeywordStatRepository.findByKeyword(tempKeyword);
        if (searchKeywordStats != null) {
            int count = searchKeywordStats.getSearchCount() + 1;
            searchKeywordStats.setSearchCount(count);
        } else {
            searchKeywordStats = PostsConverter.toSearchKeywordStats(tempKeyword);
            searchKeywordStatRepository.save(searchKeywordStats);
        }

        return PostsConverter.toViewAllList(items);
    }


    public PostsResponse.saveSearchListResponse findSearchService(String userId) {
        UserJpaEntity user = userRepository.findById(userId).orElse(null);
        List<UserSearchHistJpaEntity> searchHists = userSearchHistRepository.findByUserAndDelYnOrderByCreatedAtDesc(user, false);
        List<PostsResponse.saveSearch> result = searchHists.stream().map(searchHist -> PostsConverter.toUserSearchHist(searchHist)).toList();
        return PostsConverter.toUserSearhList(result);

    }

    public List<String> findRecommandService() {
        List<SearchKeywordStatsJpaEntity> searchKeywordStats = searchKeywordStatRepository.findAllByOrderBySearchCountDesc();
        List<String> result = searchKeywordStats.stream().map(searchKeywordStat -> PostsConverter.toRecommandSearch(searchKeywordStat)).toList();
        return result;
    }

    public PostsResponse.ReviewIdResponse DoReviewPostService(String postId, String userId, PostsRequest.ReviewRequest request) throws IOException {
        UserJpaEntity user = userRepository.findById(userId).orElse(null);
        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        BorrowHistJpaEntity borrowHist = borrowHistRepository.findTop1BorrowHistByBorrowerOrderByCreatedAt(user);
        String postsId = ULIDUtil.generatorULID("REVIEW");
        if (item == null) {
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        if (request.getRating() >= 6 || request.getRating() <= 0) {
            throw new CustomException(ErrorCode.BadRequest, "평점이 올바르지 않습니다. 1~5 사이로 입력해주셔야합니다.", HttpStatus.BAD_REQUEST);
        }
        if (user == item.getOwner()) {
            throw new CustomException(ErrorCode.BadRequest, "자기 자신의 게시물에는 리뷰작성이 안됩니다.", HttpStatus.BAD_REQUEST);
        }
        if (borrowHist == null) {
            throw new CustomException(ErrorCode.BadRequest, "해당 게시물에 대한 대여 기록이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        ItemsReviewJpaEntity review = PostsConverter.toItemsReview(user, item, request, postsId);
        itemsReivewRepository.save(review);
        PushRequest push= PushRequest.builder()
                            .userId(userId)
                            .title("리뷰 등록 알림")
                            .content(user.getNickname() +"님이 내 제품에 리뷰를 남겼어요")
                            .moveToId(item.getId())
                            .pushType(PushType.REVIEW_COMPLETE)
                            .build();
        //리뷰 알림
        pushService.sendPush(push);
        return PostsConverter.toReviewIdResponse(item, review);
    }


    public PostsResponse.NoRentalPeriodsResponse ViewNoRentalPeriodsService(String userId, String postId) {
        UserJpaEntity user = userRepository.findById(userId).orElse(null);
        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        // 상태 코드 리스트 정의
        List<String> statusCodes = List.of("RENTAL_NOT_POSSIBLE", "RENTAL_POSSIBLE");
        List<ItemsBorrowStatusJpaEntity> itemsBorrowStatus = itemsBorrowStatusRepository.findAllByItemIdAndBorrowStatusCodeIn(postId, statusCodes);

        List<PostsResponse.NoRentalPeriodResponse> ownerNoRentalPeriod = itemsBorrowStatus.stream().map(itemStatus -> {
            return PostsConverter.toOwnerNoRentalPeriod(itemStatus);
        }).toList();

        List<ChatChannelJpaEntity> chatChannel = chatRepository.findAllByItemAndContactUser(item, user);
        List<PostsResponse.NoRentalPeriodResponse> contactNoRentalPeriod = chatChannel.stream().map(chat -> {
            return PostsConverter.toContactNoRentalPeriod(chat);
        }).toList();

        return PostsConverter.toNoRentalPeriods(ownerNoRentalPeriod, contactNoRentalPeriod);

    }
    @Transactional
    @Override
    public PostsResponse.BillAcceptResponse DoBillAcceptService(String userId, String channelId) {
        UserJpaEntity user = userRepository.findById(userId).orElse(null);

        ChatChannelJpaEntity chat =chatRepository.findById(channelId).orElse(null);
        ItemsJpaEntity item = chat.getItem();
        if (item == null) {
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        if (user != chat.getOwner()){
            throw new CustomException(ErrorCode.BadRequest, "해당 채팅방에 대한 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if(chat == null){
            throw new CustomException(ErrorCode.BadRequest, " 올바른 채팅방 정보가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        BorrowHistJpaEntity borrowHist = PostsConverter.toBorrowHist(item,user, chat);
        BorrowHistJpaEntity savedBorrowHist = borrowHistRepository.save(borrowHist);

        PostsRequest.NoRentalPeriod noRentalPeriod = PostsRequest.NoRentalPeriod.builder()
                .startDate(chat.getStartedAt())
                .endDate(chat.getEndedAt())
                .build();

        ItemsBorrowStatusJpaEntity itemsBorrowStatus = PostsConverter.toItemBorrowStatus(item, "Renting", noRentalPeriod);
        itemsBorrowStatusRepository.save(itemsBorrowStatus);
        chat.setChannelState(ChannelState.CONFIRMED);
        //해당 스케줄러 백업 등록
        ReviewAlertJpaEntity reviewAlert = ReviewAlertJpaEntity.builder()
                .borrowHist(savedBorrowHist)
                .status("PENDING")
                .build();
        reviewAlertRepository.save(reviewAlert);
        //리뷰 요청 알림 등록
        reviewNotificationManager.ReviewNotification(savedBorrowHist);


        return PostsConverter.toBillAcceptResponse(savedBorrowHist.getBorrowSeq());

    }
    @Transactional
    @Override
    public String CancelBillAcceptService(String userId, String channelId){
        try {
            UserJpaEntity user = userRepository.findById(userId).orElse(null);
            ChatChannelJpaEntity chat = chatRepository.findById(channelId).orElse(null);
            ItemsJpaEntity item = chat.getItem();
            BorrowHistJpaEntity borrowHist = borrowHistRepository.findBorrowHistByItemAndStartedAtAndEndedAt(item,chat.getStartedAt(), chat.getEndedAt());

            if (user != chat.getOwner()) {
                throw new CustomException(ErrorCode.BadRequest, "해당 채팅방에 대한 권한이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (chat == null) {
                throw new CustomException(ErrorCode.BadRequest, " 올바른 채팅방 정보가 아닙니다.", HttpStatus.BAD_REQUEST);
            }
            if( borrowHist==null){
                throw new CustomException(ErrorCode.BadRequest, " 확정된 거래가 아닙니다.", HttpStatus.BAD_REQUEST);
            }
            borrowHist.setUseYn(false);
            chat.setChannelState(ChannelState.CANCELLED);

            reviewNotificationManager.CanceledReviewNotification(borrowHist);
        } catch (Exception e){
            throw new CustomException(ErrorCode.BadRequest, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return "success";
    }

    public PostsResponse.ReviewsResponse ViewReviewService(String userId, String postId,String sortBy) {

        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        List<ItemsReviewJpaEntity> itemReviews;
        switch (sortBy) {
            case "createdAt":
                itemReviews= itemsReivewRepository.findAllByItemsOrderByCreatedAtDesc(item);
                break;
            case "highest":
                itemReviews= itemsReivewRepository.findAllByItemsOrderByRatingDesc(item);
                break;
            case "lowest":
                itemReviews= itemsReivewRepository.findAllByItemsOrderByRatingAsc(item);
                break;
            default:
                itemReviews= itemsReivewRepository.findAllByItemsOrderByCreatedAtDesc(item);
                break;
        }



        List<PostsResponse.ReviewResponse> reviews = itemReviews.stream().map(itemReview -> {
            List<BorrowHistJpaEntity> borrow=borrowHistRepository.findALLBorrowHistByItemAndBorrower(item,itemReview.getUser());
            List<PostsResponse.ReviewPeriods> reviewPeriods=borrow.stream().map(borrowHistJpaEntity -> {
                return PostsConverter.toReviewPeriods(borrowHistJpaEntity);
            }).toList();
            return PostsConverter.toReview(itemReview,reviewPeriods);
        }).toList();

        return PostsConverter.toReviews(reviews);
    }

    @Override
    public void likePost(String userId, String postId) {
        ItemsJpaEntity item = itemsRepository.findById(postId).orElse(null);
        UserJpaEntity user = userRepository.findById(userId).orElse(null);

        ItemsLikeId id = ItemsLikeId.builder()
                .itemId(postId)
                .userId(userId)
                .build();

        ItemsLikeJpaEntity itemsLike = itemsLikeRepository.findById(id).orElse(null);

        if (itemsLike != null) {
            itemsLike.setDelYn(true);
        } else {
            itemsLike = ItemsLikeJpaEntity.builder()
                    .id(id)
//                    .items(item)
//                    .user(user)
                    .delYn(false)
                    .build();
        }

        itemsLikeRepository.save(itemsLike);
    }

    @Transactional
    @Override
    public void dislikePost(String userId, String postId) {
        ItemsLikeId id = ItemsLikeId.builder()
                .itemId(postId)
                .userId(userId)
                .build();

        Optional<ItemsLikeJpaEntity> itemsLike = itemsLikeRepository.findById(id);

        if(itemsLike.isEmpty()) throw new CustomException(ErrorCode.NotFound, "좋아요가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

        itemsLike.get().setDelYn(true);
        itemsLikeRepository.save(itemsLike.get());
    }

    @Transactional
    @Override
    public void deleteBorrowHistory(String userId, Long borrowSeq) {
        itemsRepository.deleteBorrowHistory(userId, borrowSeq);
    }


    public void findUserForReviews(){
        List<PostsResponse.FindUsersForReviewsResponse> results=borrowHistRepository.findUsersForReviews();
        if(results.isEmpty()){
            return;
        }
        //FCM 알림 및 알림 로그 저장
        results.stream().map(result->{
            PushRequest request=PushRequest.builder()
                    .userId(result.getUser().getUserId())
                    .title("물건을 잘 이용하셨나요?")
                    .pushType(PushType.REVIEW_ALERT)
                    .content(result.getUser().getNickname()+"님! 이용하신 "+result.getItem().getTitle()+"는 어떠셨나요? 이용 후기를 남겨주세요!")
                    .moveToId(result.getItem().getId())
                    .build();
            try {
                pushService.sendPush(request);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.BadRequest, " 리뷰 관련 알림 오류입니다. 담당자에게 문의해주세요", HttpStatus.BAD_REQUEST);
            }

            return null;
        });
        log.info("현재 시간: "+ LocalDate.now()+"리뷰 요청 체크 작업 완료했습니다.");

    }



    //모듈화 코드

    private Pageable createPageable(int page, Sort.Direction direction, String orderType) {
        //카테고리 필드
        String sortField = switch (orderType) {
            case "price" -> "price";
            case "createdAt" -> "createdAt";
            case "likeCount" -> "likeCount";
            case "distance" -> "distance";
            default -> "createdAt"; // 기본 정렬
        };

        //정렬 순서
        direction = (direction == null) ? Sort.Direction.DESC : direction;
        //페이지 생성
        return PageRequest.of(
                Math.max(0, page - 1), // 페이지 번호 조정 (0부터 시작)
                20,
                Sort.by(direction, sortField)
        );
    }

    private List<PostsResponse.Post> findAndConvertItems(String category, Pageable pageable, String keyword, Double latitude,Double longitude) {
        // Repository 호출
        Page<ItemsJpaEntity> itemsPage = itemsRepository.findItemsWithConditions(category, pageable, null, keyword, latitude, longitude);

        // 빈 결과 체크
        if (itemsPage.isEmpty()) {
            log.warn("No items found for category: {}", category);
            return List.of();
        }

        // 데이터 변환
        return itemsPage.getContent().stream()
                .map(item -> {
                    ItemsBorrowJpaEntity borrowItem = itemsBorrowRepository.findById(item.getId()).orElse(null);
                    ItemsLocationJpaEntity location = itemsLocationRepository.findById(item.getId()).orElse(null);
                    if (borrowItem == null) {
                        log.warn("No borrow item found for item ID: {}", item.getId());
                    }
                    return PostsConverter.toPost(item, borrowItem, location);
                })
                .toList();
    }
    private Double roundToDecimal(Double value) {
        if(value==null){
            return null;

        }
        return Math.round(value*1e8)/1e8;
    }


}
