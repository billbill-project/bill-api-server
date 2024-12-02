package site.billbill.apiserver.api.chat.service;

public interface WebhookService {
    void sendWebhookForChatRoomCreate(String channelId, String contact, String owner);
}
