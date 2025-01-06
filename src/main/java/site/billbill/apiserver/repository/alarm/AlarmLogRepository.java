package site.billbill.apiserver.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.alarm.AlarmLogJpaEntity;

public interface AlarmLogRepository extends JpaRepository<AlarmLogJpaEntity, Long> {
}
