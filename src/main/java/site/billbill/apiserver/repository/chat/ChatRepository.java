package site.billbill.apiserver.repository.chat;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

public interface ChatRepository extends JpaRepository<ChatChannelJpaEntity,String> {
    List<ChatChannelJpaEntity> findByItemAndCloYnFalseAndDelYnFalse(ItemsJpaEntity item);
}
