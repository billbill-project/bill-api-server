package site.billbill.apiserver.api.auth.service;

import site.billbill.apiserver.api.auth.dto.request.IdentityRequest;
import site.billbill.apiserver.api.auth.dto.request.LoginRequest;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;

public interface AuthService {
    JwtDto signup(SignupRequest request);

    JwtDto login(LoginRequest request);

    JwtDto reissue(String refreshToken);

    boolean identifyUser(IdentityRequest request);

    boolean getNicknameValidity(String nickname);

    boolean getEmailValidity(String email);
}
