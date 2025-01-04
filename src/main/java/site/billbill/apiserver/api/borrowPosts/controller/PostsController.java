package site.billbill.apiserver.api.borrowPosts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.api.borrowPosts.service.PostsService;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "borrowPosts", description = "대여 게시물 관련")
@RequestMapping("/api/v1/posts/borrowPosts")
@RequiredArgsConstructor
public class PostsController {
    private final PostsService postsService;

    @Operation(summary = "게시물 생성", description = "게시물 생성 API")
    @PostMapping("")
    public BaseResponse<PostsResponse.UploadResponse> uploadPostsController(@RequestBody @Valid PostsRequest.UploadRequest request) {

        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }

        return new BaseResponse<>(postsService.uploadPostService(request, userId));

    }

    @Operation(summary = "게시물리스트", description = "게시물 리스트 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public BaseResponse<PostsResponse.ViewAllResultResponse> getPostsController(
            @Parameter(name = "category", description = "카테고리 필터 (예: entire, camp, sports,tools )", example = "entire", in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "category", required = false) String category,
            @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1", in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @Parameter(name = "order", description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "asc", in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
            @Parameter(name = "sortBy", description = "정렬 기준 (예: price, createdAt, likeCount,distance)", example = "distance", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "sortBy", required = true, defaultValue = "accuracy") String sortBy,
            @Parameter(name="latitude",description = "위도",example = "37.0789561558879",in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "latitude",required = false)Double latitude,
            @Parameter(name="longitude",description = "경도",example =  "127.423084873712",in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "longitude",required = false)Double longitude
    ) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
         // Null 체크 추가
        if (latitude == null || longitude == null) {
            log.info("Latitude or Longitude is null. Using default sorting by createdAt.");
            sortBy = "createdAt"; // 기본 정렬로 변경
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new BaseResponse<>(postsService.ViewAllPostService(category, page, direction, sortBy,userId,latitude,longitude));
    }

    @Operation(summary = "게시물 검색", description = "게시물 검색 API")
    @GetMapping("/search")
    public BaseResponse<PostsResponse.ViewAllResultResponse> getSearchPostsController(
            @Parameter(name = "category", description = "카테고리 필터 (예: entire, camp, sports,tools )", example = "entire", in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "category", required = false) String category,
            @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1", in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @Parameter(name = "order", description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "asc", in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
            @Parameter(name = "sortBy", description = "정렬 기준 (예: price, createdAt, likeCount,distance)", example = "distance", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "sortBy", required = true, defaultValue = "accuracy") String sortBy,
            @Parameter(name = "keyword", description = "검색 키워드(예: 6인용+텐트)", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "keyword", required = true) String keyword,
            @Parameter(name="latitude",description = "위도",example = "37.0789561558879",in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "latitude",required = false)Double latitude,
            @Parameter(name="longitude",description = "경도",example =  "127.423084873712",in = ParameterIn.QUERY, required = false)
            @RequestParam(value = "longitude",required = false)Double longitude) {

        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new BaseResponse<>(postsService.ViewSearchPostService(userId, category, page, direction, sortBy, keyword, false,latitude,longitude));
    }

    @Operation(summary = "게시물 조회", description = "게시물 상세 조회")
    @GetMapping("/{postId}")
    public BaseResponse<PostsResponse.ViewPostResponse> getPostController(@PathVariable(value = "postId", required = true) String postId) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.ViewPostService(postId, userId));
    }

    @Operation(summary = "게시물 삭제", description = "게시물 삭제")
    @DeleteMapping("/{postId}")
    public BaseResponse<String> deletePostController(@PathVariable(value = "postId", required = true) String postId) {

        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.deletePostService(postId, userId));
    }
//    @Operation(summary = "저장한 검색어 불러오기", description = "저장한 검색어 불러오기")
//    @GetMapping("/searchHist")
//    public BaseResponse<PostsResponse.saveSearchListResponse> getSearchHistController(){
//        String userId = "";
//        if(MDC.get(JWTUtil.MDC_USER_ID) != null) {
//            userId=  MDC.get(JWTUtil.MDC_USER_ID);
//        }
//        return new BaseResponse<>(postsService.findSearchService(userId));
//    }

    @Operation(summary = "추천 검색어 불러오기", description = "추천 검색어 주기")
    @GetMapping("/recommend")
    public BaseResponse<List<String>> getRecommendController() {
        return new BaseResponse<>(postsService.findRecommandService());
    }

    @Operation(summary = "게시물 수정", description = "게시물 수정")
    @PatchMapping("/{postId}")
    public BaseResponse<String> updatePostController(@PathVariable(value = "postId", required = true) String postId,
                                                     @RequestBody @Valid PostsRequest.UploadRequest request) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.UpdatePostService(postId, userId, request));
    }

    @Operation(summary = "리뷰작성", description = "리뷰 작성")
    @PostMapping("/reviews/{postId}")
    public BaseResponse<PostsResponse.ReviewIdResponse> reviewPostController(@PathVariable(value = "postId", required = true) String postId,
                                                                             @RequestBody @Valid PostsRequest.ReviewRequest request) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.DoReviewPostService(postId, userId, request));
    }

    @Operation(summary = "리뷰조회", description = "리뷰 조회")
    @GetMapping("/reviews/{postId}")
    public BaseResponse<PostsResponse.ReviewsResponse> getReviewsController(@PathVariable(value = "postId", required = true) String postId,
            @Parameter(name = "sortBy", description = "정렬 기준 (예:createdAt,highest,lowest )", example = "createdAt", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.ViewReviewService(userId, postId,sortBy));
    }

    @Operation(summary = "해당 게시물 불가능한 날짜 조회", description = "해당 게시물의 불가능한 날짜 조회를 합니다. owner : 게시물을 등록할 때 작성한 게시물 대여 불가 기간입니다. user : 해당 채팅방에서, 논의중인 대여기간입니다. ")
    @GetMapping("/{postId}/blocked")
    public BaseResponse<PostsResponse.NoRentalPeriodsResponse> blockPostController(@PathVariable(value = "postId", required = true) String postId) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.ViewNoRentalPeriodsService(userId, postId));

    }

    @Operation(summary = "거래처리", description = "해당 게시물에 대한 거래처리를 합니다.")
    @PostMapping("/payment/{channelId}")
    public BaseResponse<PostsResponse.BillAcceptResponse> BillAcceptController(@PathVariable(value = "channelId", required = true) String channelId) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.DoBillAcceptService(userId, channelId));
    }
    @Operation(summary ="거래취소",description = "해당 게시물에 대한 거래 취소를 합니다. 거래 확정된 경우에 대해 취소를 해야합니다. 거래가 확정되지 않은 상태에서는 요청하면 안됩니다.")
    @DeleteMapping("/payment/{channelId}")
    public BaseResponse<String> BillCancelController(@PathVariable(value = "channelId", required = true) String channelId) {
        String userId = "";
        if (MDC.get(JWTUtil.MDC_USER_ID) != null) {
            userId = MDC.get(JWTUtil.MDC_USER_ID);
        }
        return new BaseResponse<>(postsService.CancelBillAcceptService(userId, channelId));
    }
    @Operation(summary = "게시물 좋아요", description = "해당 게시물에 좋아요를 누르는 API, 위시리스트 추가")
    @PostMapping("/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<String> likePost(@RequestBody @Valid PostsRequest.PostIdRequest request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);
        postsService.likePost(userId, request.getPostId());
        return new BaseResponse<>(null);
    }

    @Operation(summary = "게시물 좋아요 취소", description = "해당 게시물에 좋아요를 취소하는 API, 위시리스트 삭제")
    @DeleteMapping("/likes")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<String> dislikePost(@RequestBody @Valid PostsRequest.PostIdRequest request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);
        postsService.dislikePost(userId, request.getPostId());
        return new BaseResponse<>(null);
    }

    @Operation(summary = "대여 기록 삭제", description = "대여 기록 삭제 API")
    @DeleteMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<String> deletePostHistory(@RequestBody @Valid PostsRequest.BorrowHistRequest request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);
        postsService.deleteBorrowHistory(userId, request.getBorrowSeq());
        return new BaseResponse<>(null);
    }
}
