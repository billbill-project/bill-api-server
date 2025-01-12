package site.billbill.apiserver.external.firebase.fcm.dto;

import lombok.*;

@Data
@Builder
public class FcmMessageDto {
    private boolean validateOnly;
    private FcmMessageDto.Message message;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Message {
        private FcmMessageDto.Notification notification;
        private FcmMessageDto.Data data;
        private String token;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Data {
        private String title;
        private String body;
        private String link;
    }
}
