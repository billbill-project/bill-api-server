package site.billbill.apiserver.api.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.billbill.apiserver.api.chat.service.ChatService;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;

@Slf4j
@RestController
@Tag(name = "Chat", description = "Chat API")
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "채팅방 나가기", description = "채팅방 나가기 API")
    @PostMapping("/leave")
    public BaseResponse<String> chatRoomLeave(@RequestParam String channelId) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.leaveChatChannel(channelId,userId));
    }
}
