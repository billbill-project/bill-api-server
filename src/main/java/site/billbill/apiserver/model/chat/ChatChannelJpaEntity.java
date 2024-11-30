package site.billbill.apiserver.model.chat;


import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

@Entity @Builder
@Table(name = "chat_channel")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatChannelJpaEntity extends BaseTime {
    @Id
    @Column(name = "channel_id", nullable = false)
    private String channelId;
    @ManyToOne
    @JoinColumn(name="item_id")
    private ItemsJpaEntity item;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private UserJpaEntity owner;
    @ManyToOne
    @JoinColumn(name="contact_id")
    private UserJpaEntity contact;
    @Column(name = "owner_left", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean ownerLeft = false;
    @Column(name = "contact_left", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean contactLeft = false;
    @Column(name = "del_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean delYn = false;
    @Column(name = "clo_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean cloYn = false;

    public void processLeftUser(String userId) {
        cloYn = true;
        if (owner.getUserId().equals(userId)) {
            ownerLeft = true;
            return;
        }
        contactLeft = true;
    }

    public void checkAndUpdateDelete() {
        boolean allLeft = true;

        if (!ownerLeft || !contactLeft) {
            allLeft = false;
        }

        if (allLeft) {
            delYn = true;
        }
    }
}
