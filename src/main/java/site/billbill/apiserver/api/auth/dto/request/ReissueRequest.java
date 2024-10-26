package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReissueRequest {
    @Schema(description = "Refresh token", example = "Bearer token(Refresh Token)")
    private String refreshToken;
}
