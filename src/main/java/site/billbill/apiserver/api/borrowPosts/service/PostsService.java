package site.billbill.apiserver.api.borrowPosts.service;

import org.springframework.data.domain.Sort;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;

import java.util.List;

public interface PostsService {
    PostsResponse.UploadResponse uploadPostService(PostsRequest.UploadRequest request,String userId);

    PostsResponse.ViewAllResultResponse ViewAllPostService(String category, int page, Sort.Direction direction, String orderType);

    PostsResponse.ViewPostResponse ViewPostService(String postId,String userId);

    String deletePostService(String postId,String userId);

    String UpdatePostService(String postId,String userId,PostsRequest.UploadRequest request);

    PostsResponse.ViewAllResultResponse ViewSearchPostService(String userId,String category, int page, Sort.Direction direction, String orderType,String keyword,boolean state);

    PostsResponse.saveSearchListResponse findSearchService(String userId);

    PostsResponse.ReviewIdResponse DoReviewPostService(String postId,String userId,PostsRequest.ReviewRequest request);
    List<String> findRecommandService();

    PostsResponse.NoRentalPeriodsResponse ViewNoRentalPeriodsService(String userId, String postId);

    PostsResponse.BillAcceptResponse DoBillAcceptService(String userId, PostsRequest.BillAcceptRequest request);

    PostsResponse.ReviewsResponse ViewReviewService(String userId, String postId);
}
