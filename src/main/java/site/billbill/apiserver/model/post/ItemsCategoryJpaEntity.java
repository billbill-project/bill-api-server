package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsCategoryJpaEntity {

    @Id
    @Column(name ="category_id")
    private String id;

    @ManyToOne
    @JoinColumn(name="upper_cat_id",nullable = false)
    private ItemsCategoryJpaEntity upperCategory;

    @Column(name="name")
    private String name;
}
