package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AgreeRequest {
    @Schema(description = "서비스 이용약관 동의 여부", example = "true")
    private boolean serviceAgree;
    // TODO 추가로 더 들어가야 됨
}
