package site.billbill.apiserver.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.auth.domain.UserBaseInfo;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.enums.user.UserRole;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.user.UserIdentityRepository;
import site.billbill.apiserver.repository.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserIdentityRepository userIdentityRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public JwtDto signup(SignupRequest request) {
        // Check new by name & phoneNumber
        Optional<UserIdentityJpaEntity> entity =
                userIdentityRepository.findByNameAndPhoneNumber(
                        request.getIdentity().getName(),
                        request.getIdentity().getPhoneNumber()
                );

        // if user already exists
        if (entity.isPresent()) {
            throw new CustomException(ErrorCode.Conflict, "이미 존재하는 회원입니다.", HttpStatus.CONFLICT);
        }

        // if user new
        String userId = ULIDUtil.generatorULID("USER");
        UserIdentityJpaEntity userIdentity = UserIdentityJpaEntity.toJpaEntity(userId, request.getIdentity());
        UserJpaEntity user = new UserJpaEntity(new UserBaseInfo(userId, request.getNickname(), request.getProfileImage()));

        // TODO : 위치 정보 & 장치 정보 저장 로직 추가 필요

        // save new user
        userRepository.save(user);
        userIdentityRepository.save(userIdentity);

        return jwtUtil.generateJwtDto(userId, UserRole.USER);
    }
}
