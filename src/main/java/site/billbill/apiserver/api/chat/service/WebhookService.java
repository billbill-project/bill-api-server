package site.billbill.apiserver.api.chat.service;

import java.util.List;
import site.billbill.apiserver.api.chat.dto.request.WebhookRequest;

public interface WebhookService {
    void sendWebhookForChatRoomCreate(String channelId, String contact, String owner);
    WebhookRequest.ChatInfoList sendWebhookForChatList(List<String> chatRoomIds, String beforeTimestamp);
}
