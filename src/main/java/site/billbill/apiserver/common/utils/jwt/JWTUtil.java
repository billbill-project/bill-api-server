package site.billbill.apiserver.common.utils.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.enums.user.UserRole;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.exception.CustomException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JWTUtil {
    private final String secretKey;
    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private String issuer = "BillBillServer";
    private final SecretKey key;

    public static String MDC_USER_ID = "userId";
    public static String MDC_USER_ROLE = "role";


    public JWTUtil(
            @Value("${jwt.bill.secret-key}") String secretKey,
            @Value("${jwt.bill.access-token-expired}") long ACCESS_TOKEN_EXPIRE_TIME,
            @Value("${jwt.bill.refresh-token-expired}") long REFRESH_TOKEN_EXPIRE_TIME
    ) {
        this.secretKey = secretKey;
        this.ACCESS_TOKEN_EXPIRE_TIME = ACCESS_TOKEN_EXPIRE_TIME;
        this.REFRESH_TOKEN_EXPIRE_TIME = REFRESH_TOKEN_EXPIRE_TIME;

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public JwtDto generateJwtDto(String userId, UserRole role) {
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .issuer(issuer)
                .subject(userId)
                .expiration(accessTokenExpiresIn)
                .issuedAt(new Date())
                .signWith(key)
                .claim("userId", userId)
                .claim("role", role.name())
                .claim("type", "AT")
                .compact();

        String refreshToken = Jwts.builder()
                .issuer(issuer)
                .subject(userId)
                .expiration(refreshTokenExpiresIn)
                .issuedAt(new Date())
                .signWith(key)
                .claim("userId", userId)
                .claim("role", role.name())
                .claim("type", "RT")
                .compact();

        return new JwtDto(accessToken, refreshToken, "Bearer", accessTokenExpiresIn.getTime() / 1000, role.name());
    }

    public boolean isValidAccessToken(String token) {
        try {
            if(getClaims(token).get("type").equals("AT")) return true;
        } catch (ExpiredJwtException e) {
            log.error("Bill Access 토큰 시간이 만료 되었습니다. {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("Bill Access 토큰 서명값이 유효하지 않습니다. {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("Bill Access 토큰 헤더값이 유효하지 않습니다. {}", e.getMessage());
            return false;
        }

        return false;
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String putUserMDC(Claims claims) {
        String userId= claims.getSubject();
        String role = claims.get("role", String.class);

        MDC.put(MDC_USER_ID, userId);
        MDC.put(MDC_USER_ROLE, role);

        return userId;
    }

    public boolean isExpired(String token) {
        try {
            Claims claims = getClaims(token);
            if ("RT".equals(claims.get("type"))) {  // 문자열 비교는 .equals() 사용
                return false;
            }
        } catch (ExpiredJwtException e) {
            log.error("Bill Refresh 토큰 시간이 만료되었습니다. {}", e.getMessage());
            return true;
        }
        return false;
    }

    public UserRole getUserRole(String token) {
        Claims claims = getClaims(token);
         String role = claims.get("role", String.class);
         return UserRole.valueOf(role);
    }

    public boolean isValidRefreshToken(String token) {
        try {
            if(getClaims(token).get("type").equals("RT")) return true;
        } catch (ExpiredJwtException e) {
            log.error("Bill Refresh 토큰 시간이 만료 되었습니다. {}", e.getMessage());
            throw new CustomException(ErrorCode.BadRequest, "Bill Refresh 토큰 시간이 만료되었습니다. ", HttpStatus.BAD_REQUEST);
        } catch (SignatureException e) {
            log.error("Bill Refresh 토큰 서명값이 유효하지 않습니다. {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("Bill Refresh 토큰 헤더값이 유효하지 않습니다. {}", e.getMessage());
            return false;
        }

        return false;
    }
}