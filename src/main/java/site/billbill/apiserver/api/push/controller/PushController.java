package site.billbill.apiserver.api.push.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushListResponse;
import site.billbill.apiserver.api.push.service.PushService;
import site.billbill.apiserver.common.response.BaseResponse;

import java.io.IOException;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;

@Slf4j
@RestController
@Tag(name = "Push", description = "Push API")
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "404", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
})
public class PushController {
    private final PushService pushService;

    @Operation(summary = "push 발송 API", description = "상대에게 채팅 push를 발송하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public BaseResponse<Boolean> sendChatFcm(@RequestBody PushRequest request) throws IOException {
        return new BaseResponse<>(pushService.sendPush(request));
    }
    @Operation(summary = "알림조회", description = "채팅알림 조회")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/list")
    public BaseResponse<GetPushListResponse> getPushList(@RequestParam(required = false) String beforeTimestamp) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(pushService.getPushList(beforeTimestamp, userId));
    }

//    @Operation(summary = "채팅목록 조회", description = "채팅목록 조회 API")
//    @GetMapping("/list")
//    public BaseResponse<List<ViewChatInfoResponse>> getChatList(@RequestParam(required = false) String beforeTimestamp) {
//        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
//        return new BaseResponse<>(chatService.getChatList(beforeTimestamp, userId));
//    }
}
