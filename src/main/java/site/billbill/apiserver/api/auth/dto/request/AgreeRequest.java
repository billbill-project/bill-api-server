package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AgreeRequest {
    @Schema(description = "서비스 이용약관 동의 여부", example = "true")
    private boolean serviceAgree;
    @Schema(description = "개인정보 제공 동의 여부", example = "true")
    private boolean privacyAgree;
    @Schema(description = "마케팅 정보 수신 동의 여부", example = "true")
    private boolean marketingAgree;
    @Schema(description = "개인정보 제 3자 제공 동의 여부", example = "true")
    private boolean thirdPartyAgree;
}
