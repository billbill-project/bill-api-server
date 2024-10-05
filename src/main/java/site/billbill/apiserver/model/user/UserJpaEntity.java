package site.billbill.apiserver.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.api.auth.domain.UserBaseInfo;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.common.enums.user.Provider;
import site.billbill.apiserver.common.enums.user.UserRole;
import site.billbill.apiserver.model.BaseTime;

@DynamicUpdate
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity extends BaseTime {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "profile", nullable = true)
    private String profile;
    @Column(name = "confidence_score", nullable = false)
    private int confidenceScore = 0;
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = true)
    private Provider provider;
    @Column(name = "provider_id", nullable = true)
    private String providerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "dm_alarm", nullable = false)
    private boolean dmAlarm = true;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "like_alarm", nullable = false)
    private boolean likeAlarm = true;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "notification_alarm", nullable = false)
    private boolean notificationAlarm = true;

    public UserJpaEntity(UserBaseInfo info) {
        this.userId = info.getUserId();
        this.nickname = info.getNickname();
        this.profile = info.getProfileImage();
        this.provider = null;
        this.providerId = null;
        this.role = UserRole.USER;
    }
}
