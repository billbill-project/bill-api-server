package site.billbill.apiserver.api.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.billbill.apiserver.api.chat.dto.request.ChatRequest;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewChatInfoResponse;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewUnreadChatCountResponse;
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
    @PatchMapping("/{channelId}")
    public BaseResponse<String> leaveChatChannel(@PathVariable(value = "channelId") String channelId) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.leaveChatChannel(channelId,userId));
    }

    @Operation(summary = "채팅방 생성 및 id 조회", description = "빌리기 버튼 누를 때 api")
    @PostMapping("")
    public BaseResponse<String> startChannel(@RequestBody ChatRequest.borrowInfo request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.startChannel(request, userId));
    }

    @Operation(summary = "채팅방 info 조회", description = "채팅방 info 조회 API")
    @GetMapping("/{channelId}")
    public BaseResponse<ChatResponse.ViewChannelInfoResponse> getInfoChannel(@PathVariable(value = "channelId") String channelId) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.getInfoChannel(channelId, userId));
    }

    @Operation(summary = "채팅목록 조회", description = "채팅목록 조회 API")
    @GetMapping("/list")
    public BaseResponse<List<ViewChatInfoResponse>> getChatList(@RequestParam(required = false) String beforeTimestamp) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.getChatList(beforeTimestamp, userId));
    }

    @Operation(summary = "대여 날짜 변경", description = "대여 날짜 변경 API")
    @PatchMapping("/{channelId}/date")
    public BaseResponse<String> changeDate(@PathVariable(value = "channelId") String channelId, @RequestBody ChatRequest.changeDate request) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.changeDate(userId, channelId, request));
    }

    @Operation(summary = "안 읽은 메세지 수 조회", description = "안 읽은 메세지 수 조회 API")
    @GetMapping("/unreadCount")
    public BaseResponse<ViewUnreadChatCountResponse> getUnreadCount() {
        String userId = MDC.get(JWTUtil.MDC_USER_ID).toString();
        return new BaseResponse<>(chatService.getUnreadCount(userId));
    }
}
