package site.billbill.apiserver.api.borrowPosts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import site.billbill.apiserver.api.borrowPosts.controller.PostsController;
import site.billbill.apiserver.api.borrowPosts.converter.PostsConverter;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.ItemsBorrowStatusJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.ItemsBorrowRepository;
import site.billbill.apiserver.repository.borrowPosts.ItemsBorrowStatusRepository;
import site.billbill.apiserver.repository.borrowPosts.ItemsCategoryRepository;
import site.billbill.apiserver.repository.borrowPosts.ItemsRepository;
import site.billbill.apiserver.repository.user.UserRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor

@Slf4j

public class PostsServiceImpl implements PostsService {

    private final UserRepository userRepository;
    private final ItemsRepository itemsRepository;
    private final ItemsBorrowRepository itemsBorrowRepository;
    private final ItemsBorrowStatusRepository itemsBorrowStatusRepository;
    private  final ItemsCategoryRepository itemsCategoryRepository;
    public PostsResponse.UploadResponse uploadPostService(PostsRequest.UploadRequest request,String userId){
        //먼저 item 생성,
        Optional<UserJpaEntity> isUser=userRepository.findById(userId);
        String postsId = ULIDUtil.generatorULID("BORROW");

        UserJpaEntity user=new UserJpaEntity();
        if(isUser.isPresent()){
            user=isUser.get();
        }
        //Item 생성
        ItemsJpaEntity newItem= PostsConverter.toItem(postsId,request,user);
        itemsRepository.save(newItem);
        ItemsJpaEntity item=itemsRepository.findById(postsId).orElse(newItem);
        //BorrowItem 생성
        ItemsBorrowJpaEntity newBorrowItem= PostsConverter.toItemBorrow(item,request);
        itemsBorrowRepository.save(newBorrowItem);
        //대여 불가 기간 생성
        if (request.getNoRental() != null && !request.getNoRental().isEmpty()) {
        List<ItemsBorrowStatusJpaEntity> itemsBorrowStatusList = request.getNoRental().stream()
                .map(status -> PostsConverter.toItemBorrowStatus(item, "RENTAL_NOT_POSSIBLE", status))
                .toList();
        itemsBorrowStatusRepository.saveAll(itemsBorrowStatusList);
        }



        return PostsConverter.toUploadResponse(postsId);


    }
    public PostsResponse.ViewAllResultResponse ViewAllPostService(
            String category, int page, Sort.Direction direction, String orderType) {

        // 기본 정렬 필드와 방향 설정
        String sortField = switch (orderType) {
            case "price" -> "price";
            case "createdAt" -> "createdAt";
            case "likeCount" -> "likeCount";
            default -> "createdAt"; // 기본 정렬
        };

        direction = (direction == null) ? Sort.Direction.DESC : direction;

        Pageable pageable = PageRequest.of(
                Math.max(0, page - 1), // 페이지 번호 조정 (0부터 시작)
                20,
                Sort.by(direction, sortField)
        );

        // Repository 호출
        Page<ItemsJpaEntity> itemsPage = itemsRepository.findItemsWithConditions(category, pageable, sortField);

        // 빈 결과 체크
        if (itemsPage.isEmpty()) {
            log.warn("No items found for category: {}, page: {}, sortField: {}", category, page, sortField);
            return PostsConverter.toViewAllList(List.of());
        }

        // 데이터 변환
        List<PostsResponse.Post> borrowItems = itemsPage.getContent().stream()
                .map(item -> {
                    ItemsBorrowJpaEntity borrowItem = itemsBorrowRepository.findById(item.getId()).orElse(null);
                    if (borrowItem == null) {
                        log.warn("No borrow item found for item ID: {}", item.getId());
                    }
                    return PostsConverter.toPost(item, borrowItem);
                }).toList();

        return PostsConverter.toViewAllList(borrowItems);
    }

    public PostsResponse.ViewPostResponse ViewPostService(String postId){
        ItemsJpaEntity item=itemsRepository.findById(postId).orElse(null);
        ItemsBorrowJpaEntity borrowItem=itemsBorrowRepository.findById(postId).orElse(null);
        List<ItemsBorrowStatusJpaEntity> borrowStatus=itemsBorrowStatusRepository.findAllByItemIdAndBorrowStatusCode(postId,"RENTAL_NOT_POSSIBLE");
        List<PostsResponse.NoRentalPeriodResponse> noRentalPeriods=borrowStatus.stream().map(PostsConverter::toNoRentalPeriod).toList();
        if(item==null){
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        String status="";
        switch(item.getItemStatus()){
            case 1:
                status="상";
                break;
            case 2:
                status="중상";
                break;
            case 3:
                status="중";
                break;
            case 4:
                status="중하";
                break;
            case 5:
                status="하";
                break;

        }
        return PostsConverter.toViewPost(item,borrowItem,noRentalPeriods,status);

    }

    public String deletePostService(String postId,String userId){
        ItemsJpaEntity item= itemsRepository.findById(postId).orElse(null);
        UserJpaEntity user=userRepository.findById(userId).orElse(null);
        if(item==null){
            throw new CustomException(ErrorCode.BadRequest, "올바른 게시물 아이디가 아닙니다.", HttpStatus.BAD_REQUEST);
        }else if(!item.getOwner().equals(user)){
            throw new CustomException(ErrorCode.BadRequest, "해당 게시물 작성자가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        

        return "Succes";
    }
}