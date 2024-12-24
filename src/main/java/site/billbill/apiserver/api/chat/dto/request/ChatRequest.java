package site.billbill.apiserver.api.chat.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ChatRequest {
    @Getter
    @Setter
    @Builder
    public static class borrowInfo {
        private LocalDate startedAt;
        private LocalDate endedAt;
        private String postId;
    }

    @Getter
    @Setter
    @Builder
    public static class changeDate {
        private LocalDate startedAt;
        private LocalDate endedAt;
    }
}
