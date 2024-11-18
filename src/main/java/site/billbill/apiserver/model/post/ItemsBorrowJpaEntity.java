package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.common.converter.StringListConverter;
import site.billbill.apiserver.common.enums.items.PriceStandard;
import site.billbill.apiserver.common.enums.user.DeviceType;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.LocalDate;
import java.util.List;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_borrow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsBorrowJpaEntity extends BaseTime {
    @Id
    @Column(name = "item_id", nullable = false)
    private String id;
    @OneToOne
    @MapsId
    private ItemsJpaEntity items;
    @Enumerated(EnumType.STRING)
    @Column(name = "price_standard")
    private PriceStandard priceStandard;
    @Column(name = "price", nullable = false)
    private int price;
    @Column(name="deposit",nullable = false)
    private int deposit;
    @Column(name="start_date",nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name ="borrow_satus_code")
    private String borrowStatusCode;
}
