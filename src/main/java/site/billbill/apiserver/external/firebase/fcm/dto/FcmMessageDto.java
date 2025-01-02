package site.billbill.apiserver.external.firebase.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FcmMessageDto {
    private boolean validateOnly;
    private FcmMessageDto.Message message;

    @Data
    @Builder
    @AllArgsConstructor
    public static class Message {
        private FcmMessageDto.Notification notification;
        private String token;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}
