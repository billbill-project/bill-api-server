package site.billbill.apiserver.api.auth.service;

import org.springframework.stereotype.Service;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public JwtDto signup(SignupRequest request) {

        return null;
    }
}
