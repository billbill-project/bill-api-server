package site.billbill.apiserver.model.chat;


import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.common.enums.chat.ChannelState;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "started_at", nullable = false)
    private LocalDate startedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ended_at", nullable = false)
    private LocalDate endedAt;
    @Column(name = "del_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean delYn = false;
    @Column(name = "clo_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean cloYn = false;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_state")
    private ChannelState channelState = ChannelState.PRE;

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

    public UserJpaEntity getOpponent(String userId) {
        if (!owner.getUserId().equals(userId)) {
            return owner;
        }
        return contact;
    }

    public void ChangeDate(LocalDate startedAt, LocalDate endedAt) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
