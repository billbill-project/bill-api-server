package site.billbill.apiserver.model.post.emebeded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
@Builder
public class ItemsLikeId implements Serializable {
    @Column(name="item_id")
    private String itemId;
    @Column(name="user_id")
    private String userId;
}
