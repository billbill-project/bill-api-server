package site.billbill.apiserver.api.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.auth.domain.UserBaseInfo;
import site.billbill.apiserver.api.auth.dto.request.LocationRequest;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.enums.user.UserRole;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.model.user.UserLocationJpaEntity;
import site.billbill.apiserver.repository.user.UserIdentityRepository;
import site.billbill.apiserver.repository.user.UserLocationReposity;
import site.billbill.apiserver.repository.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserIdentityRepository userIdentityRepository;
    private final UserRepository userRepository;
    private final UserLocationReposity userLocationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional
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

        String encryptedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // if user new
        String userId = ULIDUtil.generatorULID("USER");
        UserIdentityJpaEntity userIdentity = UserIdentityJpaEntity.toJpaEntity(userId, request.getIdentity());
        UserJpaEntity user = new UserJpaEntity(new UserBaseInfo(userId, request.getProfileImage(), request.getNickname(), encryptedPassword));

        // TODO : 동의 여부 & 장치 정보 저장 로직 추가 필요

        // save new user
        userRepository.save(user);
        userIdentityRepository.save(userIdentity);

        // save location
        saveLocation(userId, request.getLocation());

        return jwtUtil.generateJwtDto(userId, UserRole.USER);
    }

    /**
     * Method that save location
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
