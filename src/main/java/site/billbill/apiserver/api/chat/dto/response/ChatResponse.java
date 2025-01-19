package site.billbill.apiserver.api.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class ChatResponse {
    @Getter
    @Builder
    public static class ViewChannelInfoResponse {
        String postTitle;
        String myRole;
        String channelState;
        boolean ownerLeft;
        boolean contactLeft;
        boolean isClose;
        boolean isDelete;
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

    @Getter
    @Builder
    public static class ViewChatInfoResponse {
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startedAt;
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endedAt;
        private boolean notification;
        private String channelId;
        private int unreadCount;
        private String lastChat;
        private String lastSender;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime updatedAt;
        private String opponentId;
        private String opponentNickname;
        private String opponentProfileUrl;
        private String itemFirstUrl;
    }

    @Getter
    @Builder
    public static class ViewUnreadChatCountResponse {
        private int count;
    }
}
