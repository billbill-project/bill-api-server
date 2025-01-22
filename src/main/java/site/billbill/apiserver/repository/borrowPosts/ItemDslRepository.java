package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.api.users.dto.response.BorrowHistoryResponse;
import site.billbill.apiserver.api.users.dto.response.PostHistoryResponse;
import site.billbill.apiserver.api.users.dto.response.WishlistResponse;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

import java.util.List;

public interface ItemDslRepository {

    Page<ItemsJpaEntity> findItemsWithConditions(String category, Pageable pageable, String sortField,String keyword,Double latitude,Double longitude);

    List<PostHistoryResponse> getPostHistory(String userId, Pageable pageable);

    List<BorrowHistoryResponse> getBorrowHistory(String userId, Pageable pageable, ItemHistoryType type);

    List<WishlistResponse> getWishlists(String userId, Pageable pageable);

    void deleteBorrowHistory(String userId, Long borrowSeq);

    List<PostsResponse.FindUsersForReviewsResponse> findUsersForReviews();

    Boolean CheckUsersForReviews(BorrowHistJpaEntity borrowHist);
}
