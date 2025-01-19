package site.billbill.apiserver.api.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    public WebhookServiceImpl(@Value("${chat-server.url}") String webhookUrl,
                              WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(webhookUrl).build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
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

    @Override
    public WebhookRequest.ChatInfoList sendWebhookForChatList(List<String> chatRoomIds, String beforeTimestamp) {
        if (beforeTimestamp == null) {
            beforeTimestamp = "";
        }
        String jsonResponse = webClient.post()
                .uri("/chat/list")
                .bodyValue(Map.of(
                        "chatRoomIds", chatRoomIds,
                        "beforeTimestamp", beforeTimestamp
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info(jsonResponse.toString());
        try {
            WebhookRequest.ChatInfoList result = objectMapper.readValue(jsonResponse, WebhookRequest.ChatInfoList.class);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON Parsing Error", e);
        }

    }

    @Override
    public int sendForUnreadChatCount(List<String> activeChatIdsByUserId, String userId) {
        log.info(activeChatIdsByUserId.toString());
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("chatRoomIds", activeChatIdsByUserId);

        Integer jsonResponse = webClient.post()
                .uri("/chat/unreadCount")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Integer.class)
                .block();

        return jsonResponse;
    }
}
