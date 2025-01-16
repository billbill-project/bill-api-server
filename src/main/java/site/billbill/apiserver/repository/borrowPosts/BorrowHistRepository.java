package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.LocalDate;
import java.util.List;

public interface BorrowHistRepository extends JpaRepository<BorrowHistJpaEntity,Long>,ItemDslRepository {
    BorrowHistJpaEntity findTop1BorrowHistByBorrowerOrderByCreatedAt(UserJpaEntity borrower);
    List<BorrowHistJpaEntity> findALLBorrowHistByItemAndBorrower(ItemsJpaEntity item, UserJpaEntity borrower);
    BorrowHistJpaEntity findBorrowHistByItemAndStartedAtAndEndedAt(ItemsJpaEntity item, LocalDate startedAt, LocalDate endedAt);
}
