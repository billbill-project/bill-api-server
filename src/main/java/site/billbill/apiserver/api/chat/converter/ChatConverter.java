package site.billbill.apiserver.api.chat.converter;

import java.time.LocalDate;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

public class ChatConverter {
    public static ChatChannelJpaEntity toChatChannel(String channelId, ItemsJpaEntity item, UserJpaEntity owner,
                                                     UserJpaEntity contact, LocalDate startedAt, LocalDate endedAt) {
        return ChatChannelJpaEntity.builder()
                .channelId(channelId)
                .item(item)
                .owner(owner)
                .contact(contact)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }

    public static ChatResponse.ViewChannelInfoResponse toViewChannelInfo(ChatChannelJpaEntity channel, UserJpaEntity opponent, ItemsJpaEntity item, int totalPrice,
                                                                         String status) {
        return ChatResponse.ViewChannelInfoResponse.builder()
                .opponentId(opponent.getUserId())
                .opponentNickname(opponent.getNickname())
                .opponentProfileUrl(opponent.getProfile())
                .itemFirstUrl(item.getImages().get(0))
                .postIsDel(item.isDelYn())
                .postId(item.getId())
                .totalPrice(totalPrice)
                .itemState(status)
                .startedAt(channel.getStartedAt())
                .endedAt(channel.getEndedAt())
                .build();
    }
}
