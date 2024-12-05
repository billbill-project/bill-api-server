package site.billbill.apiserver.api.s3.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor

public class S3Response {
    @Builder
    @Getter
    public static class uploadResponse{
        private List<String> urls;
    }

}
