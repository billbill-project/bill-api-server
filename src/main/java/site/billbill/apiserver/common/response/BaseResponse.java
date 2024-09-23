package site.billbill.apiserver.common.response;

import lombok.Data;

@Data
public class BaseResponse {
    private boolean isSuccess;
    private String message;
    private Object data;
}
