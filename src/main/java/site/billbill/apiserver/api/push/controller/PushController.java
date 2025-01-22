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
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendChatPushRequest;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendPushRequest;
import site.billbill.apiserver.api.push.dto.response.PushResponse;
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

    @Operation(summary = "push 발송 API", description = "상대에게 push를 발송하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public BaseResponse<Boolean> sendFcm(@RequestBody SendPushRequest request) throws IOException {
        return new BaseResponse<>(pushService.sendPush(request));
    }

    @Operation(summary = "chat push 발송 API", description = "상대에게 채팅 push를 발송하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chat")
    public BaseResponse<Boolean> sendChatFcm(@RequestBody SendChatPushRequest request) throws IOException {
        SendPushRequest sendPushRequest = pushService.sendChatPush(request);
        return new BaseResponse<>(pushService.sendPush(sendPushRequest));
    }
    //알림조회 API
    @Operation(summary = "알림조회", description = "알림을 조회하는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/list")
    public BaseResponse<GetPushListResponse> getPushList(@RequestParam(required = false) String beforeTimestamp) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(pushService.getPushList(beforeTimestamp, userId));
    }
    //리뷰알림 확인 API
    @Operation(summary = "리뷰 알림 조회",description = "팝업 알림을 띄울께 있는지 조회하는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/review")
    public BaseResponse<PushResponse.GetReviewAlertResponse> getReviewAlert(){
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(pushService.getReviewAlertService(userId));
    }
}
