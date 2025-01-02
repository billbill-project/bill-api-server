package site.billbill.apiserver.api.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProfileRequest {
    @Schema(description = "닉네임", example = "nickname")
    private String nickname;
    @Schema(description = "프로필 이미지 URL", example = "profile url")
    private String profileUrl;
}
