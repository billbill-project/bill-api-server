package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.user.UserJpaEntity;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsReviewJpaEntity extends BaseTime {

    @Id
    @Column(name="review_id")
    private String id;
    @ManyToOne
    @JoinColumn(name="item_id")
    private ItemsJpaEntity items;
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserJpaEntity user;

    @Column(name="rating",nullable = false)
    private int rating;
    @Column(name="content",nullable = false)
    private String content;

}
