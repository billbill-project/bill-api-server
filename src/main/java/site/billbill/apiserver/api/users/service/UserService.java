package site.billbill.apiserver.api.users.service;

import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.api.users.dto.response.*;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;

import java.util.List;

public interface UserService {
    ProfileResponse getProfileInfo();

    ProfileResponse getProfileInfo(String userId);

    void blockUser(String userId);

    List<BlacklistResponse> getBlacklist(Pageable pageable);

    void blockCancel(String userId);

    void withdraw();

    List<PostHistoryResponse> getPostHistory(Pageable pageable);

    List<BorrowHistoryResponse> getPostHistory(Pageable pageable, ItemHistoryType type);

    List<WishlistResponse> getWishlists(Pageable pageable);
}
