package site.billbill.apiserver.api.push.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.model.user.UserDeviceJpaEntity;
import site.billbill.apiserver.repository.user.UserDeviceRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {
    private final UserDeviceRepository userDeviceRepository;

    @Override
    public boolean sendPush(PushRequest request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);

        Optional<UserDeviceJpaEntity> userDevice = userDeviceRepository.findById(userId);



        return false;
    }

//    private String getAccessToken() {
//
//    }
}
