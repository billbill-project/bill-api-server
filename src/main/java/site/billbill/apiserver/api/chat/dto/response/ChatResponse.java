package site.billbill.apiserver.api.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

public class ChatResponse {
    @Getter
    @Builder
    public static class ViewChannelInfoResponse {
        String opponentId;
        String opponentNickname;
        String opponentProfileUrl;
        String myId;
        String itemFirstUrl;
        boolean postIsDel;
        String postId;
        int totalPrice;
        String itemState;
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startedAt;
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endedAt;
    }
}
