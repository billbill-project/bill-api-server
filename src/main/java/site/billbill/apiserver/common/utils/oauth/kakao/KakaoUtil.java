package site.billbill.apiserver.common.utils.oauth.kakao;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import site.billbill.apiserver.external.oauth.kakao.dto.response.KakaoTokenResponse;
import site.billbill.apiserver.external.oauth.kakao.dto.response.KakaoUserInfoResponse;

@Component
@Slf4j
@Getter @Setter @Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class KakaoUtil {
    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;
    @Value("${kakao.provider.token-uri}")
    private String TOKEN_URL_HOST;
    @Value("${kakao.provider.user-info-uri")
    private String USER_INFO_URL_HOST;

    public String getAccessToken(String code) {
        KakaoTokenResponse kakaoTokenResponse = WebClient.create(TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponse != null ? kakaoTokenResponse.getAccessToken() : null);
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponse != null ? kakaoTokenResponse.getRefreshToken() : null);
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponse != null ? kakaoTokenResponse.getIdToken() : null);
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponse != null ? kakaoTokenResponse.getScope() : null);

        return kakaoTokenResponse != null ? kakaoTokenResponse.getAccessToken() : null;
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {

        KakaoUserInfoResponse userInfo = WebClient.create(USER_INFO_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }
}
