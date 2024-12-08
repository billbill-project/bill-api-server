package site.billbill.apiserver.api.s3.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor

public class S3Response {
    @Builder
    @Getter
    @Schema(description = "S3 업로드 응답")
    public static class uploadS3Response {
        private List<String> urls;
    }

}
