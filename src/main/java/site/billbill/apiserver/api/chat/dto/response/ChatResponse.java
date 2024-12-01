package site.billbill.apiserver.api.chat.dto.response;

import java.time.LocalDate;
import lombok.Builder;

public class ChatResponse {
    @Builder
    public static class ViewChannelInfoResponse {
        String opponentId;
        String opponentNickname;
        String opponentProfileUrl;
        String itemFirstUrl;
        boolean postIsDel;
        String postId;
        int totalPrice;
        String itemState;
        LocalDate startedAt;
        LocalDate endedAt;
    }
}
