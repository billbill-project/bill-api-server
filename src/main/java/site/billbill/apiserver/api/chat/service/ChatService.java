package site.billbill.apiserver.api.chat.service;

import java.util.List;
import site.billbill.apiserver.api.chat.dto.request.ChatRequest;
import site.billbill.apiserver.api.chat.dto.request.ChatRequest.changeDate;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewChannelInfoResponse;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewChatInfoResponse;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewUnreadChatCountResponse;

public interface ChatService {
    String leaveChatChannel(String postId, String userId);

    ViewChannelInfoResponse getInfoChannel(String channelId, String userId);

    String startChannel(ChatRequest.borrowInfo request, String userId);

    List<ViewChatInfoResponse> getChatList(String beforeTimestamp, String userId);

    String changeDate(String userId, String channelId, changeDate request);

    ViewUnreadChatCountResponse getUnreadCount(String userId);
}
