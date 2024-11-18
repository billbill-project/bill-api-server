package site.billbill.apiserver.model.user;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;

@Entity
@Table(name = "users_agree_hist")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAgreeHistJpaEntity extends BaseTime {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "service_agree_yn")
    @Convert(converter = BooleanConverter.class)
    private boolean serviceAgreeYn;
    @Column(name = "privacy_agree_yn")
    @Convert(converter = BooleanConverter.class)
    private boolean privacyAgreeYn;
    @Column(name = "marketing_agree_yn")
    @Convert(converter = BooleanConverter.class)
    private boolean marketingAgreeYn;
    @Column(name = "third_party_agree_yn")
    @Convert(converter = BooleanConverter.class)
    private boolean thirdPartyAgreeYn;
}
