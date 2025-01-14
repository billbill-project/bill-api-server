package site.billbill.apiserver.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.model.alarm.AlarmListJpaEntity;

public interface AlarmListRepository extends JpaRepository<AlarmListJpaEntity, Long> {
    AlarmListJpaEntity findByAlarmSeqAndPushType(Long alarmSeq, PushType pushType);
}
