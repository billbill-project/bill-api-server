package site.billbill.apiserver.api.s3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class S3Response {
    @Builder
    public static class uploadResponse{
        private List<String> urls;
    }

}
