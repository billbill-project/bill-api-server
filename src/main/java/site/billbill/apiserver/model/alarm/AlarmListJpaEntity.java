package site.billbill.apiserver.model.alarm;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.model.BaseTime;

@Entity
@Builder
@Table(name = "alarm_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmListJpaEntity extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_seq", nullable = false)
    private long alarmSeq;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content")
    private String content;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "push_type")
    private PushType pushType;
    @Column(name = "move_to_id")
    private String moveToId;
}
