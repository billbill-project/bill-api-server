package site.billbill.apiserver.api.auth.service;

import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;

public interface AuthService {
    JwtDto signup(SignupRequest request);
}
