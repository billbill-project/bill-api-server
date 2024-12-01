package site.billbill.apiserver.api.chat.service;

import site.billbill.apiserver.api.chat.dto.request.ChatRequest;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewChannelInfoResponse;

public interface ChatService {
    String leaveChatChannel(String postId, String userId);

    ViewChannelInfoResponse getInfoChannel(String channelId, String userId);


    String startChannel(ChatRequest.borrowInfo request, String userId);
}
