package site.billbill.apiserver.api.borrowPosts.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class PostsResponse {
    @Builder
    @Getter
   public static class UploadResponse {
       private String postId;
   }
}
