package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.OffsetDateTime;

@Entity
@Builder
@Table(name = "borrow_hist")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistJapEntity extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrow_seq", nullable = false)
    private long borrowSeq;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemsJpaEntity item;
    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private UserJpaEntity borrower;
    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;
    @Column(name = "ended_at", nullable = false)
    private OffsetDateTime endedAt;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "use_yn", nullable = false)
    private boolean useYn = true;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "del_yn", nullable = false)
    private boolean delYn = false;
}
