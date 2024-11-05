package site.billbill.apiserver.api.s3.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3Resource;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.converter.S3Converter;
import site.billbill.apiserver.api.s3.dto.request.S3Request;
import site.billbill.apiserver.api.s3.dto.response.S3Response;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor

@Slf4j
public class S3ServiceImpl implements S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    private String Posts_IMG_DIR="posts/";
    private String CHATS_IMG_DIR="chat/";
    private String USERS_IMG_DIR="users/";
    @Override
    public S3Response.uploadResponse uploadPostsFiles(List<MultipartFile> files, long userId, long id){
        StringBuilder imgDir= new StringBuilder();
        imgDir.append(Posts_IMG_DIR)
                .append(userId)
                .append("/")
                .append(id);
        List<String>list= files.stream()
                .map(file->{
                    try {
                        return upload(file,imgDir.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

        return S3Converter.toS3UploadResponse(list);
    }

    @Override
    public S3Response.uploadResponse uploadChatFiles(List<MultipartFile> files,long userId,long id){
        return null;
    }
    @Override
    public S3Response.uploadResponse uploadUserFiles(List<MultipartFile> files){

        return null;
    }

    private String upload(MultipartFile file,String dirName) throws IOException {
        String fileName=dirName+file.getName();
        return putS3(file,fileName);
    }
    private String putS3(MultipartFile multiFile, String fileName) throws IOException {
        File file=new File(String.valueOf(UUID.randomUUID()));
        multiFile.transferTo(file);
        amazonS3Client.putObject(new PutObjectRequest(bucket,fileName, file).withCannedAcl(
                CannedAccessControlList.PublicRead
        ));
        return amazonS3Client.getUrl(bucket,fileName).toString();
    }


}
