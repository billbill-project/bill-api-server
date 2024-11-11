package site.billbill.apiserver.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.enums.user.Provider;
import site.billbill.apiserver.common.enums.user.UserRole;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.common.utils.oauth.kakao.KakaoUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.external.oauth.kakao.dto.response.KakaoUserInfoResponse;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.user.UserIdentityRepository;
import site.billbill.apiserver.repository.user.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final KakaoUtil kakaoUtil;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;

    @Override
    public JwtDto kakaoCallback(String code) {
        String accessToken = kakaoUtil.getAccessToken(code);
        if (accessToken == null)
            throw new CustomException(ErrorCode.ServerError, "토큰을 발급받는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        KakaoUserInfoResponse userInfo = kakaoUtil.getUserInfo(accessToken);

        Optional<UserJpaEntity> optionalUser = userRepository.findByProviderId(userInfo.partner.getUuid());

        // 만약 이미 가입된 회원이라면 토큰 반환
        if (optionalUser.isPresent()) {
            return jwtUtil.generateJwtDto(optionalUser.get().getUserId(), optionalUser.get().getRole());
        }

        // 만약 신규 회원이라면
        String userId = ULIDUtil.generatorULID("USER");

        String birthDateString = userInfo.kakaoAccount.getBirthYear() + userInfo.kakaoAccount.getBirthDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birth = LocalDate.parse(birthDateString, formatter);

        char gender = Character.toUpperCase(userInfo.kakaoAccount.getGender().charAt(0));

        UserJpaEntity user = UserJpaEntity.builder()
                .userId(userId)
                .email(userInfo.kakaoAccount.getEmail())
                .password(null)
                .nickname(userInfo.kakaoAccount.profile.getNickName())
                .profile(userInfo.kakaoAccount.profile.getProfileImageUrl())
                .provider(Provider.KAKAO)
                .providerId(userInfo.getId().toString())
                .role(UserRole.USER)
                .withdrawAt(null)
                .build();

        UserIdentityJpaEntity userIdentity = UserIdentityJpaEntity.builder()
                .userId(userId)
                .name(userInfo.kakaoAccount.getName())
                .phoneNumber(userInfo.kakaoAccount.getPhoneNumber().replace("+82 ", "0"))
                .birth(birth)
                .gender(gender)
                .build();

        userRepository.save(user);
        userIdentityRepository.save(userIdentity);

        // UserAgreeHist 랑 UserDevice, UserLocation 은 별도로 추가해야됨

        return jwtUtil.generateJwtDto(userId, UserRole.USER);
    }
}
