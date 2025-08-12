package site.billbill.apiserver.api.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PushDTO {
    private String targetUserId;
    private String senderId;
    private String channelId;
    private String content;
}
