package site.billbill.apiserver.external.firebase.fcm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.external.firebase.fcm.dto.FcmMessageDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FirebaseUtil {
    @Value("${firebase.type}")
    private String type;
    @Value("${firebase.project_id}")
    private String projectId;
    @Value("${firebase.private_key_id}")
    private String privateKeyId;
    @Value("${firebase.private_key}")
    private String privateKey;
    @Value("${firebase.client_email}")
    private String clientEmail;
    @Value("${firebase.client_id}")
    private String clientId;
    @Value("${firebase.auth_uri}")
    private String authUri;
    @Value("${firebase.token_uri}")
    private String tokenUri;
    @Value("${firebase.auth_provider_x509_cert_url}")
    private String authProviderX509CertUrl;
    @Value("${firebase.client_x509_cert_url}")
    private String clientX509CertUrl;
    @Value("${firebase.universe_domain}")
    private String universeDomain;

    public boolean sendFcmTo(PushRequest request, String deviceToken) throws IOException {
        String message = makeMessage(request, deviceToken);
        RestTemplate restTemplate = new RestTemplate();

        // RestTemplate 이용중 클라이언트의 한글 깨짐 증상에 대한 수정
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        String API_URL = "<https://fcm.googleapis.com/v1/projects/billbill-50fe2/messages:send>";
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        log.info("FCM Push send status : {}", response.getStatusCode());

        return response.getStatusCode() == HttpStatus.OK;
    }

    public String getAccessToken() throws IOException {
        Map<String, Object> firebaseConfig = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("type", type),
                new AbstractMap.SimpleEntry<>("project_id", projectId),
                new AbstractMap.SimpleEntry<>("private_key_id", privateKeyId),
                new AbstractMap.SimpleEntry<>("private_key", privateKey),
                new AbstractMap.SimpleEntry<>("client_email", clientEmail),
                new AbstractMap.SimpleEntry<>("client_id", clientId),
                new AbstractMap.SimpleEntry<>("auth_uri", authUri),
                new AbstractMap.SimpleEntry<>("token_uri", tokenUri),
                new AbstractMap.SimpleEntry<>("auth_provider_x509_cert_url", authProviderX509CertUrl),
                new AbstractMap.SimpleEntry<>("client_x509_cert_url", clientX509CertUrl),
                new AbstractMap.SimpleEntry<>("universe_domain", universeDomain)
        );

        String firebaseConfigJson = new ObjectMapper()
                .writeValueAsString(firebaseConfig);

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(firebaseConfigJson.getBytes()))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

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
