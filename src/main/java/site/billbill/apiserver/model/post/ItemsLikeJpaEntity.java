package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.post.embeded.ItemsLikeId;
import site.billbill.apiserver.model.user.UserJpaEntity;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemsLikeJpaEntity extends BaseTime {
    @EmbeddedId
    private ItemsLikeId id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name="item_id")
//    private ItemsJpaEntity items;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name="user_id")
//    private UserJpaEntity user;

    @Column(name = "del_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean delYn;

}
