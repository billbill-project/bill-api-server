package site.billbill.apiserver.model.user;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;

@Entity
@Table(name = "users_blacklist")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlacklistJpaEntity extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_seq", nullable = false)
    private Long blacklistSeq;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "blacked_id", nullable = false)
    private String blackedId;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "del_yn", nullable = false)
    private boolean delYn = false;
}
