package site.billbill.apiserver.api.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.repository.chat.ChatRepository;
import site.billbill.apiserver.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

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
}
