package site.billbill.apiserver.api.push.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.external.firebase.fcm.utils.FirebaseUtil;
import site.billbill.apiserver.model.user.UserDeviceJpaEntity;
import site.billbill.apiserver.repository.user.UserDeviceRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {
    private final UserDeviceRepository userDeviceRepository;
    private final FirebaseUtil firebaseUtil;

    @Override
    public boolean sendPush(PushRequest request) throws IOException {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        Optional<UserDeviceJpaEntity> userDevice = userDeviceRepository.findById(userId);

        if(userDevice.isEmpty()) throw new CustomException(ErrorCode.NotFound, "User Device 정보가 존재하지 않습니다", HttpStatus.NOT_FOUND);

        return firebaseUtil.sendFcmTo(request, userDevice.get().getDeviceToken());
    }
}
