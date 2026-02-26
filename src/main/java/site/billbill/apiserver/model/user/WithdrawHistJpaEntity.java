package site.billbill.apiserver.model.user;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.StringListConverter;
import site.billbill.apiserver.model.BaseTime;

import java.util.List;

@Entity
@Table(name = "withdraw_hist")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawHistJpaEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_seq", nullable = false)
    private Long withdrawSeq;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;
    @Convert(converter = StringListConverter.class)
    @Column(name = "withdraw_code", nullable = false)
    private List<String> withdrawCode;
    @Column(name = "detail")
    private String detail;
}
