package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.model.BaseTime;
import org.locationtech.jts.geom.Point;
import java.awt.*;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsLocationJpaEntity extends BaseTime {
    @Id
    @Column(name="item_id")
    private String id;

    @ManyToOne
    @MapsId
    @JoinColumn(name="item_id")
    private ItemsJpaEntity item;

    @Column(name="address",nullable = false)
    private String address;

    @Column(name="coordinates",nullable = false)
    private Point coordinates;

    @Column(name = "latitude", nullable = true)
    private Double latitude;
    @Column(name = "longitude", nullable = true)
    private Double longitude;



}
