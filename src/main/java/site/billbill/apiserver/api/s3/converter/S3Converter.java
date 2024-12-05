package site.billbill.apiserver.api.s3.converter;

import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.dto.request.S3Request;
import site.billbill.apiserver.api.s3.dto.response.S3Response;

import java.util.List;

public class S3Converter {
    public static S3Response.uploadResponse toS3UploadResponse(List<String> urls){
        return S3Response.uploadResponse.builder()
                .urls(urls)
                .build();
    }

}
