package site.billbill.apiserver.api.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.auth.domain.UserBaseInfo;
import site.billbill.apiserver.api.auth.dto.request.LocationRequest;
import site.billbill.apiserver.api.auth.dto.request.LoginRequest;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.enums.user.UserRole;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.user.*;
import site.billbill.apiserver.repository.user.*;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserIdentityRepository userIdentityRepository;
    private final UserRepository userRepository;
    private final UserLocationReposity userLocationRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final UserAgreeHistRepository userAgreeHistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional
    public JwtDto signup(SignupRequest request) {
        // Check new by name & phoneNumber
        boolean isExists =
                userIdentityRepository.existsByNameAndPhoneNumber(
                        request.getIdentity().getName(),
                        request.getIdentity().getPhoneNumber()
                );

        // if user already exists
        if (isExists) {
            throw new CustomException(ErrorCode.Conflict, "이미 존재하는 회원입니다.", HttpStatus.CONFLICT);
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // if user new
        String userId = ULIDUtil.generatorULID("USER");
        UserIdentityJpaEntity userIdentity = UserIdentityJpaEntity.toJpaEntity(userId, request.getIdentity());
        UserJpaEntity user = new UserJpaEntity(new UserBaseInfo(userId, request.getProfileImage(), request.getNickname(), encryptedPassword));

        UserAgreeHistJpaEntity userAgree = new UserAgreeHistJpaEntity(userId, request.getAgree().isServiceAgree(), request.getAgree().isPrivacyAgree(), request.getAgree().isMarketingAgree(), request.getAgree().isThirdPartyAgree());
        UserDeviceJpaEntity userDevice = new UserDeviceJpaEntity(userId, ULIDUtil.generatorULID("DEVICE"), request.getDevice().getDeviceToken(), request.getDevice().getDeviceType(), request.getDevice().getAppVersion());

        // save new user
        userRepository.save(user);
        userIdentityRepository.save(userIdentity);
        userDeviceRepository.save(userDevice);
        userAgreeHistRepository.save(userAgree);

        // save location
        saveLocation(userId, request.getLocation());

        return jwtUtil.generateJwtDto(userId, UserRole.USER);
    }

    @Override
    public JwtDto login(LoginRequest request) {
        // bring user's phone number
        Optional<UserIdentityJpaEntity> userIdentityJpaEntity = userIdentityRepository.findByPhoneNumber(request.getPhoneNumber());

        if (userIdentityJpaEntity.isEmpty())
            throw new CustomException(ErrorCode.NotFound, "전화번호를 확인해주세요", HttpStatus.NOT_FOUND);

        // bring user's password
        String userId = userIdentityJpaEntity.get().getUserId();
        Optional<UserJpaEntity> userJpaEntity = userRepository.findById(userId);
        if (userJpaEntity.isEmpty())
            throw new CustomException(ErrorCode.NotFound, "해당 회원이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

        String encryptedPassword = userJpaEntity.get().getPassword();
        if (!checkPassword(request.getPassword(), encryptedPassword))
            throw new CustomException(ErrorCode.Unauthorized, "비밀번호를 확인해 주세요.", HttpStatus.UNAUTHORIZED);


        return jwtUtil.generateJwtDto(userId, userJpaEntity.get().getRole());
    }

    @Override
    public JwtDto reissue(String refreshToken) {
        if (jwtUtil.isValidRefreshToken(refreshToken)) {
            String userId = jwtUtil.getClaims(refreshToken).getSubject();
            UserRole role = jwtUtil.getUserRole(refreshToken);

            if (isUserWithdraw(userId))
                throw new CustomException(ErrorCode.Unauthorized, "탈퇴한 회원입니다.", HttpStatus.UNAUTHORIZED);

            return jwtUtil.generateJwtDto(userId, role);
        } else {
            throw new CustomException(ErrorCode.BadRequest, "올바른 Refresh 토큰이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method that if user is withdrawn
     *
     * @param userId USER ID
     * @return isWithdrew true/false
     */
    private boolean isUserWithdraw(String userId) {
        // TODO 로직 구현해야 됨
        return false;
    }

    /**
     * Method that check password is right
     *
     * @param password          password plaintext
     * @param encryptedPassword password encrypted text
     * @return isPassword correct true/false
     */
    private boolean checkPassword(String password, String encryptedPassword) {
        return bCryptPasswordEncoder.matches(password, encryptedPassword);
    }

    /**
     * Method that save location
     *
     * @param location address, longitude, latitude
     */
    @Transactional
    protected void saveLocation(
            String userId,
            LocationRequest location
    ) {
        // TODO location.service 패키지로 이동 예정
        // check if location already exists
        Optional<UserLocationJpaEntity> locationJpaEntity = userLocationRepository.findByUserId(userId);
        UserLocationJpaEntity userLocation = locationJpaEntity.orElseGet(UserLocationJpaEntity::new); // if not exists, use new

        // 좌표 계산
        Point coordinates = geometryFactory.createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));

        userLocation.setUserId(userId);
        userLocation.setAddress(location.getAddress());
        userLocation.setCoordinates(coordinates);

        userLocationRepository.save(userLocation);
    }
}
