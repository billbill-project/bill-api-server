package site.billbill.apiserver.model.post;

import jakarta.mail.FetchProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.post.emebeded.ItemsLikeId;
import site.billbill.apiserver.model.user.UserJpaEntity;

@DynamicUpdate
@Entity
@Builder
@Table(name = "items_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class itemsLikeJpaEntity extends BaseTime {
    @EmbeddedId
    private ItemsLikeId id;

    @ManyToOne
    @MapsId
    @JoinColumn(name="item_id")
    private ItemsJpaEntity items;

    @ManyToOne
    @MapsId
    @JoinColumn(name="user_id")
    private UserJpaEntity user;

    @Column(name = "del_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean delYn;

}
