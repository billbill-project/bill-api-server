package site.billbill.apiserver.api.push.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data @Builder
@ToString
public class PushRequest {
    @Schema(description = "Push 를 보낼 유저 ID", example = "USER-XXXXXXX")
    private String userId;
    @Schema(description = "Push 의 상단 제목", example = "title")
    private String title;
    @Schema(description = "Push 의 내용", example = "content")
    private String content;
    @Schema(description = "채팅방의 ID", example = "CHAT-XXXXXX")
    private String chatChannelId;
}
