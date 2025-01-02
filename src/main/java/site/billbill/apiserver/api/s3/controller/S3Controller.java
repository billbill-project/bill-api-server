package site.billbill.apiserver.api.s3.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.billbill.apiserver.api.s3.Service.S3Service;
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
    @Operation(summary = "s3 이미지 업로드", description = "s3 이미지 업로드입니다. 쿼리의 id는 채팅방 id만 해당됩니다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value="/{option}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<S3Response.uploadS3Response> upload(@Valid List<MultipartFile> images,
                                                            @Parameter(name = "option", description = "S3 옵션 (user, chat, post)", example = "user", in = ParameterIn.PATH, required = true)
                                                            @PathVariable("option") String option,
                                                            @Parameter(name = "id", description = "채팅방 id", example = "CHAT-XXXXX", in = ParameterIn.QUERY, required = false)
                                                            @RequestParam(value ="id",required = false) String id) {
        String userId = "";
        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId=  MDC.get(JWTUtil.MDC_USER_ID).toString();

        }
        return switch (option) {
            case "post" -> new BaseResponse<>(s3Service.uploadPostsFiles(images, userId));
            case "chat" -> new BaseResponse<>(s3Service.uploadChatFiles(images, userId,id));
            case "user" -> new BaseResponse<>(s3Service.uploadUserFiles(images));
            default -> null;
        };


    }
}
