package site.billbill.apiserver.api.auth.service;

import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;

public interface OAuthService {
    JwtDto kakaoLogin(String code);
}
