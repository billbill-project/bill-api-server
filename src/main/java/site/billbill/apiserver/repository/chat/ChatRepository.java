package site.billbill.apiserver.repository.chat;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.billbill.apiserver.model.chat.ChatChannelJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

public interface ChatRepository extends JpaRepository<ChatChannelJpaEntity,String> {
//    List<ChatChannelJpaEntity> findByItemAndCloYnFalseAndDelYnFalse(ItemsJpaEntity item);

    @Query("SELECT c FROM ChatChannelJpaEntity c " +
            "WHERE c.item = :item " +
            "AND c.cloYn = false " +
            "AND c.delYn = false " +
            "AND c.startedAt = :startDate " +
            "AND c.endedAt = :endDate")
    List<ChatChannelJpaEntity> findByItemAndStartAndEndDate(ItemsJpaEntity item, LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM ChatChannelJpaEntity c "+
            "WHERE c.item = :item " +
            "AND c.delYn = false " +
            "And c.contact = :user "
    )
    List<ChatChannelJpaEntity> findAllByItemAndContactUser(ItemsJpaEntity item, UserJpaEntity user);

    @Query("SELECT c.channelId " +
            "FROM ChatChannelJpaEntity c " +
            "WHERE (c.owner.userId = :userId AND c.ownerLeft = false) " +
            "   OR (c.contact.userId = :userId AND c.contactLeft = false)")
    List<String> findActiveChatIdsByUserId(String userId);
}
