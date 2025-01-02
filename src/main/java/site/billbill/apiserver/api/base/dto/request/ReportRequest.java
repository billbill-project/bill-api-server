package site.billbill.apiserver.api.base.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import site.billbill.apiserver.common.enums.base.ReportCode;

import java.util.List;

@Data
public class ReportRequest {
    @Schema(description = "신고할 유저 ID", example = "USER-XXXXX")
    private String userId;
    @Schema(description = "신고 사유 코드", type = "array",example = "[\"code\", \"code\"]")
    private List<String> reportCode;
    @Schema(description = "신고 상세 사유", example = "상세 설명")
    private String description;
}
