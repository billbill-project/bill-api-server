package site.billbill.apiserver.common.utils.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
    // TODO : 마저 설정해야됨
    private final String secretKey;
    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;

    public JWTUtil(
            @Value("${jwt.bill.secret-key}") String secretKey,
            @Value("${jwt.bill.access-token-expired}") long ACCESS_TOKEN_EXPIRE_TIME,
            @Value("${jwt.bill.refresh-token-expired}") long REFRESH_TOKEN_EXPIRE_TIME
    ) {
        this.secretKey = secretKey;
        this.ACCESS_TOKEN_EXPIRE_TIME = ACCESS_TOKEN_EXPIRE_TIME;
        this.REFRESH_TOKEN_EXPIRE_TIME = REFRESH_TOKEN_EXPIRE_TIME;
    }
}
