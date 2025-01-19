package site.billbill.apiserver.api.chat.converter;

import java.time.LocalDate;
import site.billbill.apiserver.api.chat.dto.request.ChatServerRequest.ChatInfo;
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

    public static ChatResponse.ViewChannelInfoResponse toViewChannelInfo(ChatChannelJpaEntity channel,
                                                                         UserJpaEntity opponent, ItemsJpaEntity item,
                                                                         int totalPrice,
                                                                         String status, String userId, String role) {

        return ChatResponse.ViewChannelInfoResponse.builder()
                .postTitle(item.getTitle())
                .myRole(role)
                .channelState(String.valueOf(channel.getChannelState()))
                .ownerLeft(channel.isOwnerLeft())
                .contactLeft(channel.isContactLeft())
                .isClose(channel.isCloYn())
                .isDelete(channel.isDelYn())
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
                .myId(userId)
                .build();
    }

    public static ChatResponse.ViewChatInfoResponse toViewChatInfo(ChatInfo chatInfo,
                                                                   String userId, UserJpaEntity opponent,
                                                                   ChatChannelJpaEntity channel) {
        int unReadCount = chatInfo.getUnreadCount();

        if (chatInfo.getLastSender().equals(userId)) {
            unReadCount = 0;
        }

        return ChatResponse.ViewChatInfoResponse.builder()
                .startedAt(channel.getStartedAt())
                .endedAt(channel.getEndedAt())
                .channelId(chatInfo.getChannelId())
                .notification(chatInfo.isNotification())
                .lastChat(chatInfo.getLastChat())
                .lastSender(chatInfo.getLastSender())
                .updatedAt(chatInfo.getUpdatedAt())
                .unreadCount(unReadCount)
                .opponentId(opponent.getUserId())
                .opponentProfileUrl(opponent.getProfile())
                .opponentNickname(opponent.getNickname())
                .itemFirstUrl(channel.getItem().getImages().get(0))
                .build();
    }

    public static ChatResponse.ViewUnreadChatCountResponse toViewUnreadChatCount(int count) {
        return ChatResponse.ViewUnreadChatCountResponse.builder()
                .count(count)
                .build();
    }
}
