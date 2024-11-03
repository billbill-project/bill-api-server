package site.billbill.apiserver.api.s3.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class S3Request {
    @Getter
    @Setter
    @Builder
    public static class uploadReqeust{
        private List<MultipartFile> files;
        private String option;
    }
}
