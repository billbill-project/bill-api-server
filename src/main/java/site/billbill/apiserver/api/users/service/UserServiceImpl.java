package site.billbill.apiserver.api.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.auth.dto.request.DeviceRequest;
import site.billbill.apiserver.api.users.dto.response.*;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.user.UserBlacklistJpaEntity;
import site.billbill.apiserver.model.user.UserDeviceJpaEntity;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.model.user.UserLocationJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.ItemsRepository;
import site.billbill.apiserver.repository.user.UserBlacklistRepository;
import site.billbill.apiserver.repository.user.UserDeviceRepository;
import site.billbill.apiserver.repository.user.UserIdentityRepository;
import site.billbill.apiserver.repository.user.UserLocationReposity;
import site.billbill.apiserver.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final UserBlacklistRepository userBlacklistRepository;
    private final ItemsRepository itemsRepository;
    private final JWTUtil jWTUtil;
    private final UserLocationReposity userLocationReposity;
    private final UserDeviceRepository userDeviceRepository;

    @Override
    public ProfileResponse getProfileInfo() {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return getProfileInfo(userId);
    }

    @Override
    public ProfileResponse getProfileInfo(String userId) {
        Optional<UserJpaEntity> user = userRepository.findById(userId);
        Optional<UserIdentityJpaEntity> userIdentity = userIdentityRepository.findById(userId);
        Optional<UserLocationJpaEntity> userLocation = userLocationReposity.findById(userId);

        if (user.isEmpty() || userIdentity.isEmpty() || userLocation.isEmpty()) {
            throw new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        return ProfileResponse.builder()
                .userId(userId)
                .profileImage(user.get().getProfile())
                .nickname(user.get().getNickname())
                .phoneNumber(userIdentity.get().getPhoneNumber())
                .provider(user.get().getProvider())
                .location(LocationResponse.builder()
                        .address(userLocation.get().getAddress())
                        .longitude(userLocation.get().getLongitude())
                        .latitude(userLocation.get().getLatitude())
                        .build())
                .build();
    }

    @Override
    public void blockUser(String userId) {
        String currentUserId = MDC.get(JWTUtil.MDC_USER_ID);

        UserBlacklistJpaEntity userBlacklist = UserBlacklistJpaEntity.builder()
                .blacklistSeq(null)
                .userId(currentUserId)
                .blackedId(userId)
                .delYn(false)
                .build();

        userBlacklistRepository.save(userBlacklist);
    }

    @Override
    public List<BlacklistResponse> getBlacklist(Pageable pageable) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return userBlacklistRepository.getBlacklistByUserId(userId, pageable);
    }

    @Override
    public void blockCancel(String userId) {
        String currentUserId = MDC.get(JWTUtil.MDC_USER_ID);

        Optional<UserBlacklistJpaEntity> blacklist = userBlacklistRepository.findByUserIdAndBlackedId(currentUserId, userId);

        if (blacklist.isEmpty()) throw new CustomException(ErrorCode.NotFound, "차단한 회원이 아닙니다.", HttpStatus.NOT_FOUND);

        userBlacklistRepository.deleteById(blacklist.get().getBlacklistSeq());
    }

    @Override
    @Transactional
    public void withdraw() {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        Optional<UserJpaEntity> user = userRepository.findById(userId);

        if (user.isEmpty()) throw new CustomException(ErrorCode.NotFound, "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND);

        userRepository.withdrawUserById(userId);
    }

    @Override
    public List<PostHistoryResponse> getPostHistory(Pageable pageable) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return itemsRepository.getPostHistory(userId, pageable);
    }

    @Override
    public List<BorrowHistoryResponse> getPostHistory(Pageable pageable, ItemHistoryType type) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return itemsRepository.getBorrowHistory(userId, pageable, type);
    }

    @Override
    public List<WishlistResponse> getWishlists(Pageable pageable) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return itemsRepository.getWishlists(userId, pageable);
    }

    @Override
    public void updateDevice(DeviceRequest request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);
        Optional<UserDeviceJpaEntity> userDeviceOptional = userDeviceRepository.findById(userId);

        if(userDeviceOptional.isEmpty()) throw new CustomException(ErrorCode.NotFound, "디바이스 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

        UserDeviceJpaEntity userDevice = userDeviceOptional.get();

        userDevice.setDeviceToken(request.getDeviceToken());
        userDevice.setDeviceType(request.getDeviceType());
        userDevice.setAppVersion(request.getAppVersion());
        userDeviceRepository.save(userDevice);
    }
}
