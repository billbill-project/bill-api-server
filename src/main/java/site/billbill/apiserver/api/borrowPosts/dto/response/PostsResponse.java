package site.billbill.apiserver.api.borrowPosts.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
        private int price;
        private String userId;
        private String userName;
        private String createdAt;
        private int likeCount;
    }
}
