package site.billbill.apiserver.api.s3.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.Service.S3Service;
import site.billbill.apiserver.api.s3.dto.request.S3Request;
import site.billbill.apiserver.api.s3.dto.response.S3Response;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "S3", description = "S3 API")
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;
    @PostMapping("/{option}")
    public BaseResponse<S3Response.uploadResponse> upload(@Valid List<MultipartFile> images,
                                                          @PathVariable("option") String option, @RequestParam(value ="id",required = false) Long id) {
        String userId = "";
        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId=  MDC.get(JWTUtil.MDC_USER_ID).toString();

        }
        return switch (option) {
            case "post" -> new BaseResponse<>(s3Service.uploadPostsFiles(images, userId,id));
            case "chat" -> new BaseResponse<>(s3Service.uploadChatFiles(images, userId,id));
            case "user" -> new BaseResponse<>(s3Service.uploadUserFiles(images));
            default -> null;
        };


    }
}
