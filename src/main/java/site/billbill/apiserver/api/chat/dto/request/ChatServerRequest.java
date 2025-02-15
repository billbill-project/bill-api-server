package site.billbill.apiserver.api.chat.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatServerRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatInfo {
        private String channelId;
        private int unreadCount;
        private String lastChat;
        private String lastSender;
        private LocalDateTime updatedAt;
        private boolean notification;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatInfoList {
        private List<ChatInfo> chatInfoList;
    }
}
