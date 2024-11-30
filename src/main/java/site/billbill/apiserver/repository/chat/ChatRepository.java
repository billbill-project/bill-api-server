package site.billbill.apiserver.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;

public interface ChatRepository extends JpaRepository<ChatChannelJpaEntity,String> {
}
