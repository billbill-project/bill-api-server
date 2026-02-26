package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.model.BaseTime;

import java.time.LocalDate;
@DynamicUpdate
@Entity
@Builder
@Table(name = "items_borrow_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsBorrowStatusJpaEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="status_seq")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private ItemsJpaEntity item;
    @Column(name="start_date",nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name ="borrow_status_code")
    private String borrowStatusCode;
}
