package site.billbill.apiserver.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.locationtech.jts.geom.Point;
import site.billbill.apiserver.model.BaseTime;

@DynamicUpdate
@Entity
@Table(name = "users_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationJpaEntity extends BaseTime {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "address", nullable = true)
    private String address;
    @Column(name = "coordinates", nullable = true)
    private Point coordinates;
    @Column(name = "latitude", nullable = true)
    private Double latitude;
    @Column(name = "longitude", nullable = true)
    private Double longitude;
}
