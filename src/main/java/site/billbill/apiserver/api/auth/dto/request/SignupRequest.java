package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignupRequest {
    @Schema(description = "회원 프로필 이미지 URL", example = "profile image url")
    private String profileImage;
    @Schema(description = "이메일", example = "abcde@gmail.com")
    private String email;
    @Schema(description = "회원 닉네임", example = "nickname")
    private String nickname;
    @Schema(description = "비밀번호", example = "password")
    private String password;
//    @Schema(description = "회원 본인인증 정보")
//    private IdentityRequest identity;
    @Schema(description = "각종 약관 동의")
    private AgreeRequest agree;
    @Schema(description = "위치 정보")
    private LocationRequest location;
    @Schema(description = "디바이스 정보")
    private DeviceRequest device;
}