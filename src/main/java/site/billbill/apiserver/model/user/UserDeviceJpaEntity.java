package site.billbill.apiserver.model.user;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.enums.user.DeviceType;
import site.billbill.apiserver.model.BaseTime;

@Entity
@Table(name = "users_device")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceJpaEntity extends BaseTime {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name="device_id", nullable = false)
    private String deviceId;
    @Column(name = "device_token", nullable = false)
    private String deviceToken;
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;
    @Column(name = "app_version")
    private String appVersion;
}
