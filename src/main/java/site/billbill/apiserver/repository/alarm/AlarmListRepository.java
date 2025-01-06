package site.billbill.apiserver.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.alarm.AlarmListJpaEntity;

public interface AlarmListRepository extends JpaRepository<AlarmListJpaEntity, Long> {
}
