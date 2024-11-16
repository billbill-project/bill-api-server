package site.billbill.apiserver.common.utils;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import site.billbill.apiserver.api.auth.dto.request.IdentityVerificationRequest;

@Component
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class PortOneUtil {
    @Value("${portone.api-secret}")
    private String apiSecret;
    private WebClient webClient = WebClient.builder().baseUrl("https://api.portone.io").build();

    public boolean getIdentityVerification(IdentityVerificationRequest request) {
        return false;
    }
}
