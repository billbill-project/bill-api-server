package site.billbill.apiserver.api.s3.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AwsFileServiceImpl implements AwsFileService{
    private final AmazonS3Client amazonS3Client;

    @Override
    public List<String> uploadPostsFiles(List<MultipartFile> files){
        String
        List<String>list= files.stream()
                .map(file->{
                    amazonS3Client.putObject(new put)


        });
        return list;
    }
    @Override
    public List<String> uploadChatFiles(List<MultipartFile> files){
        return null;
    }
    @Override
    public List<String> uploadUserFiles(List<MultipartFile> files){
        return null;
    }

}
