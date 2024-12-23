package site.billbill.apiserver.api.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import site.billbill.apiserver.api.chat.dto.request.WebhookRequest;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.exception.CustomException;

@Slf4j
@Service
public class WebhookServiceImpl implements WebhookService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookServiceImpl(@Value("${webhook.url}") String webhookUrl,
                              WebClient.Builder webClientBuilder,
                              ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(webhookUrl).build();
        this.objectMapper = objectMapper;
    }

    public void sendWebhookForChatRoomCreate(String channelId, String contact, String owner) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("channelId", channelId);
        payload.put("contactId", contact);
        payload.put("ownerId", owner);

        try {
            webClient.post()
                    .uri("/channel")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Webhook 호출 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.ServerError, "Webhook 호출 실패", HttpStatus.BAD_GATEWAY);
        }
    }

    public WebhookRequest.ChatInfoList sendWebhookForChatList(List<String> chatRoomIds, String beforeTimestamp) {
        String jsonResponse = webClient.post()
                .uri("/chat/list")
                .bodyValue(Map.of(
                        "chatRoomIds", chatRoomIds,
                        "beforeTimestamp", beforeTimestamp
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            return objectMapper.readValue(jsonResponse, WebhookRequest.ChatInfoList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
