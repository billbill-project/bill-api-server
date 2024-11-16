package site.billbill.apiserver.api.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    @Schema(description = "유저 ID", example = "USER-XXXXX")
    private String userId;
    @Schema(description = "프로필 이미지 URL", example = "http://~~~")
    private String profileImage;
    @Schema(description = "닉네임", example = "nickname")
    private String nickname;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
}
