package site.billbill.apiserver.api.borrowPosts.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.billbill.apiserver.common.enums.items.PriceStandard;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.util.List;


public class PostsResponse {
    @Builder
    @Getter
    public static class UploadResponse {
       private String postId;
    }
    @Builder
    @Getter
    public static class ViewAllResultResponse{
        private List<Post> result;
    }
    @Builder
    @Getter
    public static class Post{
        private String postId;
        private String image;
        private String title;
        private int price;
        private String userId;
        private String userName;
        private String userProfile;
        private String createdAt;
        private int likeCount;
        private String categoryId;
        private String categoryName;
        private String address;
    }
    @Builder
    @Getter
    public static class BillAcceptResponse{
        private Long bilId;
    }
    @Builder
    @Getter
    public static class ViewPostResponse{
        private String postId;
        private String title;
        private String content;
        private String itemStatus;
        private List<String> images;
        private int price;
        private PriceStandard priceStandard;
        private int deposit;
        private String categoryId;
        private String categoryName;
        private String userId;
        private String userName;
        private boolean like;

    }
    @Getter
    @Setter
    @Builder
    public static class saveSearchListResponse{
        private List<saveSearch> results;
    }
    @Getter
    @Setter
    @Builder
    public static class saveSearch{
        private Long id;
        private String keyword;
    }

    @Getter
    @Setter
    @Builder
    public static class NoRentalPeriodResponse{
        private String startDate;
        private String endDate;
    }
    @Getter
    @Setter
    @Builder
    public static class NoRentalPeriodsResponse{

        private List<PostsResponse.NoRentalPeriodResponse> owner;
        private List<PostsResponse.NoRentalPeriodResponse> user;
    }
    @Getter
    @Setter
    @Builder
    public static class ReviewIdResponse{
        private String itemId;
        private String reviewId;
    }
    @Getter
    @Setter
    @Builder
    public static class ReviewResponse{
        private String reviewId;
        private String content;
        private String reviewDate;
        private int rating;
        private String UserId;
        private String UserName;
        private String UserProfile;
        private List<ReviewPeriods> reviewPeriods;
    }
    @Getter
    @Setter
    @Builder
    public static class ReviewPeriods {
        private String startTime;
        private String endTime;
    }
    @Getter
    @Setter
    @Builder
    public static class ReviewsResponse{
        private List<ReviewResponse> reviews;
    }
    @Getter
    @Setter
    @Builder
    public static class FindUsersForReviewsResponse{
        private UserJpaEntity user;
        private ItemsJpaEntity item;
    }
}
