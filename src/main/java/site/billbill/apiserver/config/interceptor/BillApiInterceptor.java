package site.billbill.apiserver.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.exception.CustomException;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillApiInterceptor implements HandlerInterceptor {
    private final JWTUtil jwtUtil;

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null) {
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                // if header format is right
                if (header.length() < 8) {
                    throw new CustomException(ErrorCode.Unauthorized, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
                }

                String subString = header.substring(7);
                if (!StringUtils.hasText(subString)) {
                    throw new CustomException(ErrorCode.Unauthorized, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
                }

                return subString;
            }

        }

        return null;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        // Unique ID for each request value
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute("LOG_ID", uuid);
        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            log.info("pass OPTION method");
            return true;
        }

        // 메서드 정보를 로깅 처리 하기 위해 추가
        HandlerMethod hd = null;
        if (handler instanceof HandlerMethod) {
            hd = (HandlerMethod) handler;
        }

        // token check
        String token = resolveToken(request);

        if (token != null && jwtUtil.isValidAccessToken(token)) {
            // if token is valid
            // 관리자용 api가 생길 경우 해당 예외처리 필요

            log.info("UserRole : {}", jwtUtil.getUserRole(token));

            String userId = jwtUtil.putUserMDC(jwtUtil.getClaims(token));
            // log 출력
            log.info("REQUEST [{}][{}] : auth by user {}", uuid, requestURI, userId);

            return true;
        }

        log.info("REQUEST [{}][{}] : no auth by user", uuid, requestURI);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        throw new CustomException(ErrorCode.Unauthorized, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = String.valueOf(request.getAttribute("LOG_ID"));

        log.info("RESPONSE[{}][{}]", logId, requestURI);
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
