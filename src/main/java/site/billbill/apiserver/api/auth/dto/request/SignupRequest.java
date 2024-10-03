package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignupRequest {
    @Schema(description = "회원 프로필 이미지 URL", example = "profile image url")
    private String profileImage;
    @Schema(description = "회원 닉네임", example = "nickname")
    private String nickname;
    @Schema(description = "회원 본인인증 정보")
    private IdentityRequest identity;
}