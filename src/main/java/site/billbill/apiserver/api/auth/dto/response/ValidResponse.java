package site.billbill.apiserver.api.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidResponse {
    private boolean valid;
}
