package site.billbill.apiserver.api.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordCheckRequest {
    @Schema(description = "기존 비밀번호", example = "original password")
    private String password;
}
