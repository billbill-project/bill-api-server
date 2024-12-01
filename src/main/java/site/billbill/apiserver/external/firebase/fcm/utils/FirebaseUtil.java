package site.billbill.apiserver.external.firebase.fcm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.external.firebase.fcm.dto.FcmMessageDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class FirebaseUtil {

    public boolean sendFcmTo(PushRequest request, String deviceToken) throws IOException {
        String message = makeMessage(request, deviceToken);
        RestTemplate restTemplate = new RestTemplate();

        // RestTemplate 이용중 클라이언트의 한글 깨짐 증상에 대한 수정
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity entity = new HttpEntity<>(message, headers);

        String API_URL = "<https://fcm.googleapis.com/v1/projects/adjh54-a0189/messages:send>";
        ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        log.info("FCM Push send status : {}", response.getStatusCode());

        return response.getStatusCode() == HttpStatus.OK;
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/bill-firebase-api-key.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refresh();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(PushRequest request, String deviceToken) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(deviceToken)
                        .notification((FcmMessageDto.Notification.builder()
                                .title(request.getTitle())
                                .body(request.getContent())
                                .image(null)
                                .build())
                        ).build())
                .validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }
}
