package site.billbill.apiserver.common.enums.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BadRequest(400, 400),
    Unauthorized(401, 401),
    Forbidden(403, 403),
    NotFound(404, 404),
    Conflict(409, 409),
    ServerError(500, 500);

    final int code;
    final int status;
}
