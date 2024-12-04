package site.billbill.apiserver.api.users.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class BlacklistResponse {
    @Schema(description = "차단 고유 seq", example = "8")
    private Long blackedSeq;
    @Schema(description = "차단한 회원 ID", example = "USER-XXXXX")
    private String blackedUserId;
    @Schema(description = "차단한 회원 프로필 URL", example = "http://~")
    private String blackedUserProfile;
    @Schema(description = "차단한 회원 닉네임", example = "nickname")
    private String blackedUserNickname;
    @Schema(description = "차단 일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime createdAt;
    @Schema(description = "수정 일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime updatedAt;
}
