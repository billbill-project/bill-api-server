package site.billbill.apiserver.common.enums.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    Unauthorized(401, 401),
    Forbidden(403, 403),
    NotFound(404, 404),
    InvalidArgument(405, 405),
    Conflict(409, 409),
    BadRequest(400, 400),
    ServerError(500, 500);

    final int code;
    final int status;
}
