package site.billbill.apiserver.api.users.service;

import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.api.auth.dto.request.DeviceRequest;
import site.billbill.apiserver.api.auth.dto.request.LocationRequest;
import site.billbill.apiserver.api.users.dto.request.AgreeRequest;
import site.billbill.apiserver.api.users.dto.request.PasswordRequest;
import site.billbill.apiserver.api.users.dto.request.ProfileRequest;
import site.billbill.apiserver.api.users.dto.request.WithdrawRequest;
import site.billbill.apiserver.api.users.dto.response.*;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;
import site.billbill.apiserver.model.common.CodeDetailJpaEntity;

import java.util.List;

public interface UserService {
    ProfileResponse getProfileInfo();

    ProfileResponse getProfileInfo(String userId);

    void blockUser(String userId);

    List<BlacklistResponse> getBlacklist(Pageable pageable);

    void blockCancel(String userId);

    void withdraw(WithdrawRequest request);

    List<PostHistoryResponse> getPostHistory(String userId, Pageable pageable);

    List<BorrowHistoryResponse> getPostHistory(Pageable pageable, ItemHistoryType type);

    List<WishlistResponse> getWishlists(Pageable pageable);

    void updateDevice(DeviceRequest request);

    void saveLocation(String userId, LocationRequest location);

    Boolean checkOriginalPassword(String password);

    void updatePassword(PasswordRequest request);

    List<CodeDetailJpaEntity> getWithdrawCodeList();

    void updateProfile(ProfileRequest request);

    void updateAgreement(AgreeRequest request);
}
