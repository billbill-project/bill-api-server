package site.billbill.apiserver.api.s3.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.converter.S3Converter;
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
    private final AmazonS3 amazonS3Client;

    private String Posts_IMG_DIR="posts/";
    private String CHATS_IMG_DIR="chats/";
    private String USERS_IMG_DIR="users/";
    @Override
    public S3Response.uploadS3Response uploadPostsFiles(List<MultipartFile> files, String userId){

        StringBuilder imgDir= new StringBuilder();
        imgDir.append(Posts_IMG_DIR)
                .append(File.separator) // 운영 체제에 맞는 구분자를 추가
                .append(userId)
                .append(File.separator)
                .append("posts");
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
    public S3Response.uploadS3Response uploadChatFiles(List<MultipartFile> files, String userId, String id){
        StringBuilder imgDir= new StringBuilder();
        imgDir.append(CHATS_IMG_DIR)
                .append(File.separator) // 운영 체제에 맞는 구분자를 추가
                .append(userId)
                .append(File.separator)
                .append(id);
        List<String>list=files.stream()
                .map(file->{
                    try{
                        return upload(file,imgDir.toString());
                    }catch(IOException e){
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        return S3Converter.toS3UploadResponse(list);
    }
    @Override
    public S3Response.uploadS3Response uploadUserFiles(List<MultipartFile> files){

        List<String>list=files.stream()
                .map(file->{
                    try{
                        return upload(file,USERS_IMG_DIR);
                    }catch(IOException e){
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        return S3Converter.toS3UploadResponse(list);

    }

    private String upload(MultipartFile file, String dirName) throws IOException {
        // 고유한 파일 이름 생성 (UUID + 원본 파일 이름)
        String fileExtension = "jpg"; // 기본 확장자를 jpg로 설정
        String originalFilename = file.getOriginalFilename();

        // 원본 파일 이름에서 확장자 추출
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        // 고유한 파일 이름 생성
        String fileName = dirName + "/" + UUID.randomUUID() + "." + fileExtension;
        return putS3(file, fileName);
    }
    private String putS3(MultipartFile multiFile, String fileName) throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);
        multiFile.transferTo(tempFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multiFile.getContentType());

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, tempFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)
                .withMetadata(metadata));
        String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        tempFile.delete(); // 업로드 후 임시 파일 삭제
        return fileUrl;
    }


}
