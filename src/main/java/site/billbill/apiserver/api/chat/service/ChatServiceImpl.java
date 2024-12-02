package site.billbill.apiserver.api.chat.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.chat.converter.ChatConverter;
import site.billbill.apiserver.api.chat.dto.request.ChatRequest;
import site.billbill.apiserver.api.chat.dto.response.ChatResponse;
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
public class ChatServiceImpl implements ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ItemsRepository itemsRepository;
    private final ItemsBorrowRepository itemsBorrowRepository;
    private final WebhookServiceImpl webhookService;

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

    @Transactional
    public String startChannel(ChatRequest.borrowInfo request, String userId) {
        log.info("post찾기");
        ItemsJpaEntity item = itemsRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        log.info("회원찾기");
        UserJpaEntity contact = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        log.info("찾기완료");
        List<ChatChannelJpaEntity> chatChannel = chatRepository.findByItemAndCloYnFalseAndDelYnFalse(item);
        String newChannelId = ULIDUtil.generatorULID("CHANNEL");

        if (chatChannel.isEmpty()) {
            ChatChannelJpaEntity newChatChannel = ChatConverter.toChatChannel(newChannelId, item, item.getOwner(),
                    contact, request.getStartedAt(), request.getEndedAt());
            chatRepository.save(newChatChannel);
            webhookService.sendWebhookForChatRoomCreate(newChannelId, userId, item.getOwner().getUserId());
            return newChannelId;
        }
        return chatChannel.get(0).getChannelId();
    }

    public ChatResponse.ViewChannelInfoResponse getInfoChannel(String channelId, String userId) {
        ChatChannelJpaEntity chatChannel = chatRepository.findById(channelId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NotFound, "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        ItemsJpaEntity item = chatChannel.getItem();
        ItemsBorrowJpaEntity itemBorrow = itemsBorrowRepository.findByItem(item);

        long daysBetween = ChronoUnit.DAYS.between(chatChannel.getStartedAt(), chatChannel.getEndedAt());
        int totalPrice = (int) (daysBetween * itemBorrow.getPrice());
        UserJpaEntity opponent = chatChannel.getOpponent(userId);

        String status = switch (item.getItemStatus()) {
            case 1 -> "상";
            case 2 -> "중상";
            case 3 -> "중";
            case 4 -> "중하";
            case 5 -> "하";
            default -> "";
        };

        return ChatConverter.toViewChannelInfo(chatChannel, opponent, item, totalPrice, status);
    }
}
