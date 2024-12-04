package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "전화번호", example = "010-0000-0001")
    private String phoneNumber;
    @Schema(description = "비밀번호", example = "password")
    private String password;
}
