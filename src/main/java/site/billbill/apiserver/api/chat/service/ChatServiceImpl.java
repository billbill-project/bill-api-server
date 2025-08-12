package site.billbill.apiserver.api.chat.service;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.chat.converter.ChatConverter;
import site.billbill.apiserver.api.chat.dto.request.ChatRequest.borrowInfo;
import site.billbill.apiserver.api.chat.dto.request.ChatRequest.changeDate;
import site.billbill.apiserver.api.chat.dto.request.ChatServerRequest.ChatInfo;
import site.billbill.apiserver.api.chat.dto.request.ChatServerRequest.ChatInfoList;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewChannelInfoResponse;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewChatInfoResponse;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse.ViewUnreadChatCountResponse;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendChatPushRequest;
import site.billbill.apiserver.api.push.service.PushService;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.ItemsBorrowRepository;
import site.billbill.apiserver.repository.borrowPosts.ItemsRepository;
import site.billbill.apiserver.repository.chat.ChatRepository;
import site.billbill.apiserver.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ItemsRepository itemsRepository;
    private final ItemsBorrowRepository itemsBorrowRepository;
    private final ChatServerServiceImpl chatServerService;
    private final PushService pushService;

    @Override
    @Transactional
    public String leaveChatChannel(String channelId, String userId) {
        ChatChannelJpaEntity chatChannel = chatRepository.findById(channelId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        chatChannel.processLeftUser(userId);
        chatChannel.checkAndUpdateDelete();
        chatRepository.save(chatChannel);

        return "success";
    }

    @Override
    @Transactional
    public String startChannel(borrowInfo request, String userId) {
        ItemsJpaEntity item = itemsRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        UserJpaEntity contact = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        List<ChatChannelJpaEntity> chatChannel = chatRepository.findByItemAndStartAndEndDate(item, request.getStartedAt(), request.getEndedAt());
        String newChannelId = ULIDUtil.generatorULID("CHANNEL");

        if (chatChannel.isEmpty()) {
            ChatChannelJpaEntity newChatChannel = ChatConverter.toChatChannel(newChannelId, item, item.getOwner(),
                    contact, request.getStartedAt(), request.getEndedAt());
            chatRepository.save(newChatChannel);
            chatServerService.CreateChannel(newChannelId, userId, item.getOwner().getUserId());
            return newChannelId;
        }
        return chatChannel.get(0).getChannelId();
    }

    @Override
    public ViewChannelInfoResponse getInfoChannel(String channelId, String userId) {
        ChatChannelJpaEntity chatChannel = chatRepository.findById(channelId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        ItemsJpaEntity item = chatChannel.getItem();
        ItemsBorrowJpaEntity itemBorrow = itemsBorrowRepository.findByItem(item);

        long daysBetween = ChronoUnit.DAYS.between(chatChannel.getStartedAt(), chatChannel.getEndedAt()) + 1;
        int totalPrice = (int) (daysBetween * itemBorrow.getPrice());
        String role = "owner";
        UserJpaEntity opponent = chatChannel.getOpponent(userId);

        String status = switch (item.getItemStatus()) {
            case 1 -> "상";
            case 2 -> "중상";
            case 3 -> "중";
            case 4 -> "중하";
            case 5 -> "하";
            default -> "";
        };

        if (chatChannel.getContact().getUserId().equals(userId)) {
            role = "contact";
        }

        return ChatConverter.toViewChannelInfo(chatChannel, opponent, item, totalPrice, status, userId, role);
    }

    @Override
    public List<ViewChatInfoResponse> getChatList(String beforeTimestamp, String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<String> activeChatIdsByUserId = chatRepository.findActiveChatIdsByUserId(userId);

        if (activeChatIdsByUserId == null || activeChatIdsByUserId.isEmpty()) {
            return Collections.emptyList();
        }

        ChatInfoList result = chatServerService.getChatList(activeChatIdsByUserId, beforeTimestamp, userId);
        if (result == null || result.getChatInfoList() == null || result.getChatInfoList().isEmpty()) {
            return Collections.emptyList();
        }

        List<ChatInfo> chatInfoList = result.getChatInfoList();

        return chatInfoList.stream().map(chatInfo -> {
            ChatChannelJpaEntity chatChannel = chatRepository.findById(chatInfo.getChannelId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            UserJpaEntity opponent = chatChannel.getOpponent(userId);
            return ChatConverter.toViewChatInfo(chatInfo, userId, opponent, chatChannel);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String changeDate(String userId, String channelId, changeDate request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        ChatChannelJpaEntity chatChannel = chatRepository.findById(channelId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        boolean isExist = false;
        if (chatChannel.getOwner().getUserId().equals(userId) || chatChannel.getContact().getUserId().equals(userId)) {
            isExist = true;
        }
        if (!isExist) {
            throw new CustomException(ErrorCode.BadRequest, "채팅방 참여자가 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        chatChannel.ChangeDate(request.getStartedAt(), request.getEndedAt());
        return "success";
    }

    @Override
    public ViewUnreadChatCountResponse getUnreadCount(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<String> activeChatIdsByUserId = chatRepository.findActiveChatIdsByUserId(userId);

        if (activeChatIdsByUserId == null || activeChatIdsByUserId.isEmpty()) {
            return ChatConverter.toViewUnreadChatCount(0);
        }

        int count = chatServerService.getUnreadChatCount(activeChatIdsByUserId, userId);

        return ChatConverter.toViewUnreadChatCount(count);
    }

    @Override
    @RabbitListener(queues = "push.queue")
    public void receivePushRequest(SendChatPushRequest request) {
        log.info("Push 요청 수신 성공: {}", request);
        pushService.sendChatPush(request);
    }
}
