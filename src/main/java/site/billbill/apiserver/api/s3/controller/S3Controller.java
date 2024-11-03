package site.billbill.apiserver.api.s3.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.billbill.apiserver.api.s3.Service.AwsFileService;
import site.billbill.apiserver.api.s3.dto.request.S3Request;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;

@Slf4j
@RestController
@Tag(name = "S3", description = "S3 API")
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class S3Controller {
    @PostMapping("")
    public BaseResponse<S3Request.uploadReqeust> upload(@Valid @RequestBody S3Request request) {

        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
            long userId= (long) MDC.get(JWTUtil.MDC_USER_ID);
        }

         return new BaseResponse<>(AwsFileService.);
    }
}
