package site.billbill.apiserver.api.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.auth.dto.request.DeviceRequest;
import site.billbill.apiserver.api.auth.dto.request.LocationRequest;
import site.billbill.apiserver.api.auth.service.AuthService;
import site.billbill.apiserver.api.users.dto.request.PasswordRequest;
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
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final UserBlacklistRepository userBlacklistRepository;
    private final ItemsRepository itemsRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserLocationReposity userLocationRepository;
    private final UserDeviceRepository userDeviceRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public ProfileResponse getProfileInfo() {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        return getProfileInfo(userId);
    }

    @Override
    public ProfileResponse getProfileInfo(String userId) {
        Optional<UserJpaEntity> user = userRepository.findById(userId);
        Optional<UserIdentityJpaEntity> userIdentity = userIdentityRepository.findById(userId);
        Optional<UserLocationJpaEntity> userLocation = userLocationRepository.findById(userId);

        if (user.isEmpty() || userIdentity.isEmpty()) {
            throw new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        LocationResponse location = userLocation.map(userLocationJpaEntity -> LocationResponse.builder()
                .address(userLocationJpaEntity.getAddress())
                .longitude(userLocationJpaEntity.getLongitude())
                .latitude(userLocationJpaEntity.getLatitude())
                .build()).orElse(null);

        return ProfileResponse.builder()
                .userId(userId)
                .profileImage(user.get().getProfile())
                .nickname(user.get().getNickname())
                .phoneNumber(userIdentity.get().getPhoneNumber())
                .provider(user.get().getProvider())
                .location(location)
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

        if (userDeviceOptional.isEmpty())
            throw new CustomException(ErrorCode.NotFound, "디바이스 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

        UserDeviceJpaEntity userDevice = userDeviceOptional.get();

        userDevice.setDeviceToken(request.getDeviceToken());
        userDevice.setDeviceType(request.getDeviceType());
        userDevice.setAppVersion(request.getAppVersion());
        userDeviceRepository.save(userDevice);
    }

    /**
     * Method that save location
     *
     * @param location address, longitude, latitude
     */
    @Override
    @Transactional
    public void saveLocation(
            String userId,
            LocationRequest location
    ) {
        userId = Objects.requireNonNullElse(userId, MDC.get(JWTUtil.MDC_USER_ID));

        // check if location already exists
        Optional<UserLocationJpaEntity> locationJpaEntity = userLocationRepository.findByUserId(userId);
        UserLocationJpaEntity userLocation = locationJpaEntity.orElseGet(UserLocationJpaEntity::new); // if not exists, use new

        // 좌표 계산
        Point coordinates = geometryFactory.createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));

        userLocation.setUserId(userId);
        userLocation.setLatitude(location.getLatitude());
        userLocation.setLongitude(location.getLongitude());
        userLocation.setAddress(location.getAddress());
        userLocation.setCoordinates(coordinates);

        userLocationRepository.save(userLocation);
    }

    @Override
    public void updatePassword(PasswordRequest request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);
        UserJpaEntity user = userRepository.findById(userId).orElseThrow();

        if (!checkPassword(request.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.Unauthorized, "비밀번호를 확인해 주세요.", HttpStatus.UNAUTHORIZED);

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
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
}
