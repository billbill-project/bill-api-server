package site.billbill.apiserver.api.users.dto.request;

import lombok.Data;

@Data
public class AgreeRequest {
    boolean marketingAgree;
    boolean pushAgree;
    boolean thirdPartyAgree;
}
