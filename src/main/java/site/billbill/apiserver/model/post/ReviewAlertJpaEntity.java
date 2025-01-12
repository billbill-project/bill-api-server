package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.LocalDate;

@Entity
@Builder
@Table(name = "review_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAlertJpaEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_seq", nullable = false)
    private long alert_seq;
    @OneToOne
    @JoinColumn(name = "borrow_seq", nullable = false)
    private BorrowHistJpaEntity borrowHist;
    @Column(name = "status", nullable = false)
    private String status;
}



