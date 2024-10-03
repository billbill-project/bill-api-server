package site.billbill.apiserver.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBaseInfo {
    private String userId;
    private String nickname;
    private String profileImage;
}
