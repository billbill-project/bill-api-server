package site.billbill.apiserver.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.common.utils.oauth.kakao.KakaoUtil;
import site.billbill.apiserver.external.oauth.kakao.dto.response.KakaoUserInfoResponse;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final KakaoUtil kakaoUtil;

    @Override
    public JwtDto kakaoLogin(String code) {
        String accessToken = kakaoUtil.getAccessToken(code);
        KakaoUserInfoResponse userInfo = kakaoUtil.getUserInfo(accessToken);

        return null;
    }
}
