package site.billbill.apiserver.api.s3.Service;

import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.dto.response.S3Response;

import java.util.List;

public interface S3Service {
    S3Response.uploadS3Response uploadPostsFiles(List<MultipartFile> files, String userId);
    S3Response.uploadS3Response uploadChatFiles(List<MultipartFile> files, String userId, String id);
    S3Response.uploadS3Response uploadUserFiles(List<MultipartFile> files);

}
