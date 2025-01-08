package site.billbill.apiserver.api.push.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.external.firebase.fcm.utils.FirebaseUtil;
import site.billbill.apiserver.model.alarm.AlarmListJpaEntity;
import site.billbill.apiserver.model.alarm.AlarmLogJpaEntity;
import site.billbill.apiserver.model.user.UserDeviceJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.alarm.AlarmListRepository;
import site.billbill.apiserver.repository.alarm.AlarmLogRepository;
import site.billbill.apiserver.repository.user.UserDeviceRepository;
import site.billbill.apiserver.repository.user.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {
    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;
    private final AlarmListRepository alarmListRepository;
    private final AlarmLogRepository alarmLogRepository;
    private final FirebaseUtil firebaseUtil;

    @Override
    public boolean sendPush(PushRequest request) throws IOException {
        Optional<UserDeviceJpaEntity> userDevice = userDeviceRepository.findById(request.getUserId());
        Optional<UserJpaEntity> user = userRepository.findByUserIdAndDmAlarmIsTrue(request.getUserId());
        if (userDevice.isEmpty())
            throw new CustomException(ErrorCode.NotFound, "User Device 정보가 존재하지 않습니다", HttpStatus.NOT_FOUND);

        if (request.getPushType() != PushType.CHAT) {
            AlarmListJpaEntity alarmList = AlarmListJpaEntity.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .pushType(request.getPushType())
                    .moveToId(request.getMoveToId())
                    .build();

            alarmList = alarmListRepository.save(alarmList);

            AlarmLogJpaEntity alarmLog = AlarmLogJpaEntity.builder()
                    .userId(request.getUserId())
                    .alarmSeq(alarmList.getAlarmSeq())
                    .build();

            alarmLogRepository.save(alarmLog);
        }

        return user.isEmpty() || firebaseUtil.sendFcmTo(request, userDevice.get().getDeviceToken());
    }
}
