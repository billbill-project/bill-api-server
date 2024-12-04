package site.billbill.apiserver.api.chat.service;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class WebhookServiceImpl {
    private final WebClient webClient;

    @Value("${webhook.url}")
    private String webhookUrl;

    @Autowired
    public WebhookServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(webhookUrl).build();
    }

    public void sendWebhookForChatRoomCreate(String channelId, String contact, String owner) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("channelId", channelId);
        payload.put("contactId", contact);
        payload.put("ownerId", owner);

        webClient.post()
                .uri("")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> log.error("Webhook 호출 실패: {}", error.getMessage()))
                .subscribe();
    }
}
