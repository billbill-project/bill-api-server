package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MailRequest {
    @Schema(description = "이메일", example = "abcde@gmail.com")
    private String email;
}
