package site.billbill.apiserver.api.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.users.dto.response.ProfileResponse;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.user.UserIdentityRepository;
import site.billbill.apiserver.repository.user.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final JWTUtil jWTUtil;

    @Override
    public ProfileResponse getProfileInfo() {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return getProfileInfo(userId);
    }

    @Override
    public ProfileResponse getProfileInfo(String userId) {
        Optional<UserJpaEntity> user = userRepository.findById(userId);
        Optional<UserIdentityJpaEntity> userIdentity = userIdentityRepository.findById(userId);

        if (user.isEmpty() || userIdentity.isEmpty()) {
            throw new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        return ProfileResponse.builder()
                .userId(userId)
                .profileImage(user.get().getProfile())
                .nickname(user.get().getNickname())
                .phoneNumber(userIdentity.get().getPhoneNumber())
                .build();
    }
}
