package site.billbill.apiserver.api.borrowPosts.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.api.borrowPosts.service.PostsService;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;

@Slf4j
@RestController
@Tag(name = "borrowPosts", description = "대여 게시물 관련")
@RequestMapping("/api/v1/posts/borrowPosts")
@RequiredArgsConstructor
public class PostsController {
    private final PostsService postsService;
    @PostMapping("")
    public BaseResponse<PostsResponse.UploadResponse> uploadPostsController(@RequestBody @Valid PostsRequest.UploadRequest request){

        String userId = "";
        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId=  MDC.get(JWTUtil.MDC_USER_ID).toString();
        }

        return new BaseResponse<>(postsService.uploadPostService(request,userId));

    }

}
