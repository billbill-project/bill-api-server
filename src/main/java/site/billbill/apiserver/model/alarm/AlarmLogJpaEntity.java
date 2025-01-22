package site.billbill.apiserver.model.alarm;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;

@Entity
@Builder
@Table(name = "alarm_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmLogJpaEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_seq", nullable = false)
    private long logSeq;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "alarm_seq", nullable = false)
    private long alarmSeq;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "read_yn", nullable = false)
    private boolean readYn = false;

    public void changeRead() {
        readYn = true;
    }
}
