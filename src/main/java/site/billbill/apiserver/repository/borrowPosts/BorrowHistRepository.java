package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.LocalDate;
import java.util.List;

public interface BorrowHistRepository extends JpaRepository<BorrowHistJpaEntity,Long> {
    BorrowHistJpaEntity findTop1BorrowHistByBorrowerOrderByCreatedAt(UserJpaEntity borrower);
    List<BorrowHistJpaEntity> findALLBorrowHistByItemAndBorrower(ItemsJpaEntity item, UserJpaEntity borrower);
    BorrowHistJpaEntity findBorrowHistByItemAndBorrowerAndStartedAtAndEndedAt(ItemsJpaEntity item,UserJpaEntity borrower,  LocalDate startedAt, LocalDate endedAt);
}
