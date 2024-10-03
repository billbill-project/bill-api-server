package site.billbill.apiserver.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private boolean isSuccess = true;
    @Schema(description = "응답 코드", example = "SUCCESS")
    private String code = "SUCCESS";
    @Schema(description = "응답 메세지", example = "string")
    private String message;
    @Schema(description = "응답일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime responseAt = OffsetDateTime.now();
    @Schema(description = "응답 데이터")
    private T data;

    public BaseResponse(T data) {
        this.data = data;
    }
}
