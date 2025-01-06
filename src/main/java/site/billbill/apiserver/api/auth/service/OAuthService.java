package site.billbill.apiserver.api.auth.service;

import site.billbill.apiserver.api.auth.dto.response.OAuthLoginResponse;

public interface OAuthService {
    OAuthLoginResponse kakaoLogin(String code);
}
