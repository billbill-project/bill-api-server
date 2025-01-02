package site.billbill.apiserver.api.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordRequest {
    @Schema(description = "이메일", example = "abcde@gmail.com")
    private String email;
    @Schema(description = "새 비밀번호", example = "new password")
    private String newPassword;
}
