package site.billbill.apiserver.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BaseResponse {
    private boolean isSuccess;
    private String code = "SUCCESS";
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime responseAt = OffsetDateTime.now();
    private Object data;
}
