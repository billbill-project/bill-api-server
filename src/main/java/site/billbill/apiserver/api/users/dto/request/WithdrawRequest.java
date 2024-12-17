package site.billbill.apiserver.api.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class WithdrawRequest {
    @Schema(description = "탈퇴 사유 코드", type = "array",example = "[\"TOO_EXPENSIVE\", \"ETC\"]")
    private List<String> code;
    @Schema(description = "탈퇴 사유 상세", example = "detail withdraw reason")
    private String detail;
}
