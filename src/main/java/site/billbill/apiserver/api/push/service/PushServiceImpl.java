package site.billbill.apiserver.api.push.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.push.converter.PushConverter;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushListResponse;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
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
        if (userDevice.isEmpty()) {
            throw new CustomException(ErrorCode.NotFound, "User Device 정보가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        }

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

    @Transactional
    @Override
    public GetPushListResponse getPushList(String beforeTimestampStr, String userId) {
        //읽음 처리
        List<AlarmLogJpaEntity> byReadYnIsFalse = alarmLogRepository.findUnreadAlarmExcludingReviewAlert();
        for (AlarmLogJpaEntity log : byReadYnIsFalse) {
            log.changeRead();
        }
        //커서 페이징
        OffsetDateTime beforeTimestamp = null;
        if (beforeTimestampStr != null && !beforeTimestampStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                beforeTimestamp = OffsetDateTime.parse(beforeTimestampStr, formatter);
            } catch (DateTimeParseException e) {
                throw new CustomException(ErrorCode.BadRequest, "잘못된 날짜 형식입니다.", HttpStatus.BAD_REQUEST);
            }
        }

        Pageable pageable = PageRequest.of(0, 10);
        OffsetDateTime oneWeekAgo = OffsetDateTime.now(ZoneOffset.UTC)
                .minusWeeks(1)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);

        List<Long> alarmSeqs = alarmLogRepository.findAlarmSeqListByUserIdAndBeforeTimestamp(
                userId,
                beforeTimestamp,
                oneWeekAgo,
                pageable
        );

        List<AlarmListJpaEntity> alarms = new ArrayList<>();
        for (Long seq : alarmSeqs) {
            AlarmListJpaEntity alarmListJpaEntity = alarmListRepository.findById(seq)
                    .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            alarms.add(alarmListJpaEntity);
        }

        return PushConverter.toViewPushList(alarms);
    }
}
