package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.common.converter.StringListConverter;
import site.billbill.apiserver.model.BaseTime;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_exchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsExchangeJpaEntity extends BaseTime {
    @Id
    @Column(name="item_id")
    private String item_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="item_id")
    private ItemsJpaEntity item;
    @Convert(converter = StringListConverter.class)
    @JoinColumn(name="wisiList",nullable = false)
    String wishList;
}
