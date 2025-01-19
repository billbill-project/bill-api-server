package site.billbill.apiserver.api.chat.service;

import java.util.List;
import site.billbill.apiserver.api.chat.dto.request.ChatServerRequest;

public interface ChatServerService {
    void CreateChannel(String channelId, String contact, String owner);
    ChatServerRequest.ChatInfoList getChatList(List<String> chatRoomIds, String beforeTimestamp, String userId);

    int getUnreadChatCount(List<String> activeChatIdsByUserId, String userId);
}
