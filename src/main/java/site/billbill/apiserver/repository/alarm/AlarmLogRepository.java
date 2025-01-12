package site.billbill.apiserver.repository.alarm;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.catalina.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.billbill.apiserver.model.alarm.AlarmLogJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

public interface AlarmLogRepository extends JpaRepository<AlarmLogJpaEntity, Long> {

    @Query("SELECT a.alarmSeq " +
            "FROM AlarmLogJpaEntity a " +
            "WHERE a.userId = :userId " +
            "AND a.createdAt >= :oneWeekAgo " +
            "AND (:beforeTimestamp IS NULL OR a.createdAt < :beforeTimestamp) " +
            "ORDER BY a.createdAt DESC")
    List<Long> findAlarmSeqListByUserIdAndBeforeTimestamp(
            @Param("userId") String userId,
            @Param("beforeTimestamp") OffsetDateTime beforeTimestamp,
            @Param("oneWeekAgo") OffsetDateTime oneWeekAgo,
            Pageable pageable
    );

    List<AlarmLogJpaEntity> findByReadYnIsFalse();
    List<AlarmLogJpaEntity> findByUserIdAndReadYnIsFalse(String userId);
}
