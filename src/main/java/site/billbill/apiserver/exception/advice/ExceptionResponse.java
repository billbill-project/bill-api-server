package site.billbill.apiserver.exception.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private final Boolean success;
    private final Payload data;

    @Getter
    @AllArgsConstructor
    public static class Payload {
        private final int code;
        private final Object message;
    }
}
