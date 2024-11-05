package site.billbill.apiserver.api.s3.Service;

import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.dto.request.S3Request;
import site.billbill.apiserver.api.s3.dto.response.S3Response;

import java.util.List;

public interface S3Service {
    S3Response.uploadResponse uploadPostsFiles(List<MultipartFile> files, long userId, long id);
    S3Response.uploadResponse uploadChatFiles(List<MultipartFile> files,long userId,long id);
    S3Response.uploadResponse uploadUserFiles(List<MultipartFile> files);

}
