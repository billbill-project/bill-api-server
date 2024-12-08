package site.billbill.apiserver.api.s3.converter;

import site.billbill.apiserver.api.s3.dto.response.S3Response;

import java.util.List;

public class S3Converter {
    public static S3Response.uploadS3Response toS3UploadResponse(List<String> urls){
        return S3Response.uploadS3Response.builder()
                .urls(urls)
                .build();
    }

}
