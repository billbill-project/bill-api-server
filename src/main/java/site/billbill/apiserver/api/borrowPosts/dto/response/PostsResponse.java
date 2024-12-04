package site.billbill.apiserver.api.borrowPosts.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.common.enums.items.PriceStandard;

import java.time.LocalDate;
import java.time.OffsetDateTime;
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
        private String createdAt;
        private int likeCount;
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
        private List<NoRentalPeriodResponse> noRentalPeriod;
        private String categoryId;
        private String categoryName;

    }
    @Getter
    @Setter
    @Builder
    public static class NoRentalPeriodResponse{
        private String startDate;
        private String endDate;
    }
}
