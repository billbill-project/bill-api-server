package site.billbill.apiserver.api.borrowPosts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "게시물리스트", description = "게시물 리스트 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public BaseResponse<PostsResponse.ViewAllResultResponse> getPostsController(
            @Parameter(name = "category", description = "카테고리 필터 (예: entire, camp, sports,tools )", example = "entire", in = ParameterIn.QUERY, required = false)
            @RequestParam(value ="category",required = false,defaultValue = "entire") String category,
            @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1", in = ParameterIn.QUERY, required = false)
            @RequestParam(value ="page",required = false,defaultValue = "1") int page,
            @Parameter(name = "order", description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc", in = ParameterIn.QUERY, required = false)
            @RequestParam(value ="order",required = false,defaultValue = "desc") String order,
            @Parameter(name = "sortBy", description = "정렬 기준 (예: price, createdAt, likeCount)", example = "createdAt", in = ParameterIn.QUERY, required = true)
            @RequestParam(value="sortBy",required = true,defaultValue = "accuracy") String sortBy){


        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new BaseResponse<>(postsService.ViewAllPostService(category,page,direction,sortBy));
    }
    @GetMapping("/{postId}")
    public BaseResponse<PostsResponse.ViewPostResponse> getPostController(@PathVariable(value = "postId",required = true)String postId){

        return new BaseResponse<>(postsService.ViewPostService(postId));
    }
    @DeleteMapping("/{postId}")
    public BaseResponse<String> deletePostController(@PathVariable(value = "postId",required = true)String postId){

        String userId = "";
        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId=  MDC.get(JWTUtil.MDC_USER_ID).toString();
        }
        return new BaseResponse<>(postsService.deletePostService(postId,userId));
    }
    @PatchMapping("/{postId}")
    public BaseResponse<String> updatePostController(@PathVariable(value="postId",required = true)String postId,
                                                     @RequestBody @Valid PostsRequest.UploadRequest request){
        String userId = "";
        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId=  MDC.get(JWTUtil.MDC_USER_ID).toString();
        }
        return new BaseResponse<>(postsService.UpdatePostService(postId,userId,request));
    }


}
