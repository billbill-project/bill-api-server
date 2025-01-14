package site.billbill.apiserver.repository.alarm;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.billbill.apiserver.model.alarm.AlarmLogJpaEntity;

public interface AlarmLogRepository extends JpaRepository<AlarmLogJpaEntity, Long> {

    @Query("SELECT a.alarmSeq " +
            "FROM AlarmLogJpaEntity a " +
            "JOIN AlarmListJpaEntity b " +
            "ON a.alarmSeq = b.alarmSeq " +
            "WHERE a.userId = :userId " +
            "AND a.createdAt >= :oneWeekAgo " +
            "AND (:beforeTimestamp IS NULL OR a.createdAt < :beforeTimestamp) " +
            "AND b.pushType != 'REVIEW_ALERT' " +
            "ORDER BY a.createdAt DESC")
    List<Long> findAlarmSeqListByUserIdAndBeforeTimestamp(
            @Param("userId") String userId,
            @Param("beforeTimestamp") OffsetDateTime beforeTimestamp,
            @Param("oneWeekAgo") OffsetDateTime oneWeekAgo,
            Pageable pageable
    );

    @Query("SELECT a " +
            "FROM AlarmLogJpaEntity a " +
            "JOIN AlarmListJpaEntity b " +
            "ON a.alarmSeq = b.alarmSeq " +
            "WHERE a.readYn = false " +
            "AND b.pushType != 'REVIEW_ALERT' ")
    List<AlarmLogJpaEntity> findUnreadAlarmExcludingReviewAlert();
}
