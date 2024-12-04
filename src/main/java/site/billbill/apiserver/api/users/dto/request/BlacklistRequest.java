package site.billbill.apiserver.api.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BlacklistRequest {
    @Schema(description = "차단 회원 ID", example = "USER-XXXXX")
    private String userId;
}
