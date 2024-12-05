package site.billbill.apiserver.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;
@DynamicUpdate
@Entity
@Table(name = "users_search_hist")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchHistJpaEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_seq", nullable = false)
    private Long searchId;
    @Column(name = "keyword", nullable = true)
    private String keyword;
    @Column(name = "del_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean delYn;
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserJpaEntity user;

}
