package site.billbill.apiserver.api.s3.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class S3Request {
    @Getter
    @Setter
    public static class uploadRequest {
        private long id;
    }
}
