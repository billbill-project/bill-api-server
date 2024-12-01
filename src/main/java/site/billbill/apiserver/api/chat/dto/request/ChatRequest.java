package site.billbill.apiserver.api.chat.dto.request;

import java.time.LocalDate;
import lombok.Getter;

public class ChatRequest {
    @Getter
    public static class borrowInfo {
        private LocalDate startedAt;
        private LocalDate endedAt;
        private String postId;
    }
}
