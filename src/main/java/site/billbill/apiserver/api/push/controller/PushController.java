package site.billbill.apiserver.api.push.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.api.push.service.PushService;
import site.billbill.apiserver.common.response.BaseResponse;

import java.io.IOException;

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

    @Operation(summary = "채팅 push 발송 API", description = "상대에게 채팅 push를 발송하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chat")
    public BaseResponse<Boolean> sendChatFcm(@RequestBody PushRequest request) throws IOException {
        return new BaseResponse<>(pushService.sendPush(request));
    }
}
