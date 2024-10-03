package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignupRequest {
    @Schema(description = "회원 이메일", example = "example@example.com")
    private String email;
}
