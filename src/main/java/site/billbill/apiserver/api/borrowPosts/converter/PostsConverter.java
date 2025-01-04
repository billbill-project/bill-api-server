package site.billbill.apiserver.api.borrowPosts.converter;


import org.locationtech.jts.geom.Point;
import site.billbill.apiserver.api.auth.dto.request.LocationRequest;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.model.post.*;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.model.user.UserLocationJpaEntity;
import site.billbill.apiserver.model.user.UserSearchHistJpaEntity;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

public class PostsConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static PostsResponse.UploadResponse toUploadResponse(String id){
        return PostsResponse.UploadResponse.builder().
                postId(id).
                build();
    }
    public static ItemsLocationJpaEntity toItemsLocation(Point coordinate, LocationRequest location, ItemsJpaEntity item) {
        return ItemsLocationJpaEntity.builder()
                .item(item)
                .address(location.getAddress())
                .coordinates(coordinate)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
    public static ItemsJpaEntity toItem(String postId, PostsRequest.UploadRequest request, UserJpaEntity user, ItemsCategoryJpaEntity category){
        return ItemsJpaEntity.builder()
                .id(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .delYn(false)
                .owner(user)
                .viewCount(0)
                .images(request.getImages())
                .category(category)
                .itemStatus(request.getItemStatus()).build();
    }
    public static ItemsBorrowJpaEntity toItemBorrow(ItemsJpaEntity item, PostsRequest.UploadRequest request){
        return ItemsBorrowJpaEntity.builder()
                .item(item)
                .priceStandard(request.getPriceStandard())
                .price(request.getPrice())
                .deposit(request.getDeposit())
                .build();
    }
    public static ItemsBorrowStatusJpaEntity toItemBorrowStatus(ItemsJpaEntity item, String status, PostsRequest.NoRentalPeriod noRentalPeriod){
        return ItemsBorrowStatusJpaEntity.builder().startDate(noRentalPeriod.getStartDate())
                .endDate(noRentalPeriod.getEndDate())
                .borrowStatusCode(status)
                .item(item)
                .build();
    }

    public static PostsResponse.Post toPost(ItemsJpaEntity item, ItemsBorrowJpaEntity borrowItem, ItemsLocationJpaEntity location){
        return PostsResponse.Post.builder()
                .postId(item.getId())
                .title(item.getTitle())
                .image(Optional.ofNullable(item.getImages())
                        .filter(images -> !images.isEmpty())
                        .map(images -> images.get(0))
                        .orElse(null))
                .price(borrowItem.getPrice())
                .userId(item.getOwner().getUserId())
                .userName(item.getOwner().getNickname())
                .createdAt(item.getCreatedAt().format(DATE_TIME_FORMATTER))
                .likeCount(item.getLikeCount())
                .address(location.getAddress())
                .categoryId(Optional.ofNullable(item.getCategory())
                    .map(category -> category.getId())
                    .orElse(null))
                .categoryName(Optional.ofNullable(item.getCategory())
                    .map(category -> category.getName())
                    .orElse(null))
                .userProfile(item.getOwner().getProfile())
                .build();
    }
    public static PostsResponse.ViewAllResultResponse toViewAllList(List<PostsResponse.Post> posts){
        return PostsResponse.ViewAllResultResponse.builder().result(posts).build();
    }
    public static PostsResponse.ReviewPeriods toReviewPeriods(BorrowHistJpaEntity borrow){
        return PostsResponse.ReviewPeriods.builder()
                .startTime(borrow.getStartedAt().format(DATE_FORMATTER))
                .endTime(borrow.getEndedAt().format(DATE_FORMATTER))
                .build();
    }
    public static PostsResponse.ReviewResponse toReview(ItemsReviewJpaEntity review,List<PostsResponse.ReviewPeriods>reviewPeriods){
        return PostsResponse.ReviewResponse.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .reviewDate(review.getCreatedAt().format(DATE_TIME_FORMATTER))
                .rating(review.getRating())
                .UserId(review.getUser().getUserId())
                .UserName(review.getUser().getNickname())
                .UserProfile(review.getUser().getProfile())
                .reviewPeriods(reviewPeriods)
                .build();
    }
    public static PostsResponse.ReviewsResponse toReviews(List<PostsResponse.ReviewResponse> reviews){
        return PostsResponse.ReviewsResponse.builder().reviews(reviews).build();
    }
    public static PostsResponse.ViewPostResponse toViewPost(ItemsJpaEntity item, ItemsBorrowJpaEntity borrowItem, String status,boolean isLike){
        return PostsResponse.ViewPostResponse.builder()
                .postId(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .images(item.getImages())
                .price(borrowItem.getPrice())
                .priceStandard(borrowItem.getPriceStandard())
                .deposit(borrowItem.getDeposit())
                .itemStatus(status)
                .categoryId(Optional.ofNullable(item.getCategory())
                    .map(category -> category.getId())
                    .orElse(null))
                .categoryName(Optional.ofNullable(item.getCategory())
                    .map(category -> category.getName())
                    .orElse(null))
                .userId(item.getOwner().getUserId())
                .userName(item.getOwner().getNickname())
                .like(isLike)
                .build();
    }
    public static PostsResponse.NoRentalPeriodResponse toOwnerNoRentalPeriod(ItemsBorrowStatusJpaEntity borrowStatus){
        return PostsResponse.NoRentalPeriodResponse.builder()
                .startDate(borrowStatus.getStartDate().format(DATE_FORMATTER))
                .endDate(borrowStatus.getEndDate().format(DATE_FORMATTER))
                .build();
    }
    public static PostsResponse.NoRentalPeriodResponse toContactNoRentalPeriod(ChatChannelJpaEntity chat){
        return PostsResponse.NoRentalPeriodResponse.builder()
                .startDate(chat.getStartedAt().format(DATE_FORMATTER))
                .endDate(chat.getEndedAt().format(DATE_FORMATTER))
                .build();
    }
    public static PostsResponse.NoRentalPeriodsResponse toNoRentalPeriods(List<PostsResponse.NoRentalPeriodResponse> owner, List<PostsResponse.NoRentalPeriodResponse> user){
        return PostsResponse.NoRentalPeriodsResponse.builder()
                .owner(owner)
                .user(user)
                .build();
    }
    public static UserSearchHistJpaEntity toUserSearch(UserJpaEntity user,String keyword){
        return UserSearchHistJpaEntity.builder()
                .keyword(keyword)
                .user(user).build();
    }
    public static SearchKeywordStatsJpaEntity toSearchKeywordStats(String keyword){
        return SearchKeywordStatsJpaEntity.builder()
                .keyword(keyword)
                .searchCount(1).build();
    }
    public static PostsResponse.saveSearch toUserSearchHist(UserSearchHistJpaEntity userSeachHistory){
        return PostsResponse.saveSearch.builder().id(userSeachHistory.getSearchId())
                .keyword(userSeachHistory.getKeyword()).build();
    }
    public static PostsResponse.saveSearchListResponse toUserSearhList(List<PostsResponse.saveSearch> savedSearches){
        return PostsResponse.saveSearchListResponse.builder().results(savedSearches).build();
    }
    public static String toRecommandSearch(SearchKeywordStatsJpaEntity searchKeywordStats){
        return searchKeywordStats.getKeyword();
    }
    public static BorrowHistJpaEntity toBorrowHist(ItemsJpaEntity item,UserJpaEntity user,ChatChannelJpaEntity chat){
        return BorrowHistJpaEntity.builder()
                .item(item)
                .borrower(chat.getContact())
                .startedAt(chat.getStartedAt())
                .endedAt(chat.getEndedAt())
                .build();
    }
    public static ItemsReviewJpaEntity toItemsReview(UserJpaEntity user,ItemsJpaEntity item,PostsRequest.ReviewRequest request ,String reviewId){
        return ItemsReviewJpaEntity.builder()
                .id(reviewId)
                .items(item)
                .user(user)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

    }
    public static PostsResponse.ReviewIdResponse toReviewIdResponse(ItemsJpaEntity item, ItemsReviewJpaEntity review){
        return PostsResponse.ReviewIdResponse.builder()
                .itemId(item.getId())
                .reviewId(review.getId())
                .build();
    }
    public static PostsResponse.BillAcceptResponse toBillAcceptResponse(Long billId){
        return PostsResponse.BillAcceptResponse.builder()
                .bilId(billId)
                .build();
    }


}
