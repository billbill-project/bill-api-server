package site.billbill.apiserver.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.exception.CustomException;

@Component
public class InternalAuthInterceptor implements HandlerInterceptor {
    @Value("${internal.api-secret}")
    private String expectedSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!request.getRequestURI().equals("/api/v1/push/chat")) {
            return true;
        }

        String header = request.getHeader("secretKey");

        if (!expectedSecret.equals(header)) {
            throw new CustomException(ErrorCode.Forbidden, "Invalid secret key", HttpStatus.FORBIDDEN);
        }

        return true;
    }
}
