package site.billbill.apiserver.api.borrowPosts.service;

import org.springframework.data.domain.Sort;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;

public interface PostsService {
    PostsResponse.UploadResponse uploadPostService(PostsRequest.UploadRequest request,String userId);

    PostsResponse.ViewAllResultResponse ViewAllPostService(String category, int page, Sort.Direction direction, String orderType);

    PostsResponse.ViewPostResponse ViewPostService(String postId);
}
