package site.billbill.apiserver.api.borrowPosts.service;

import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;

public interface PostsService {
    PostsResponse.UploadResponse uploadPostService(PostsRequest.UploadRequest request,String userId);
}
