package site.billbill.apiserver.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBaseInfo {
    private String userId;
    private String profileImage;
    private String nickname;
    private String password;
}
