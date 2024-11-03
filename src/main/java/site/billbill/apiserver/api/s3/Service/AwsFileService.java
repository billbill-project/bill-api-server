package site.billbill.apiserver.api.s3.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AwsFileService {
    List<String> uploadPostsFiles(List<MultipartFile> files);
    List<String> uploadChatFiles(List<MultipartFile> files);
    List<String> uploadUserFiles(List<MultipartFile> files);

}
