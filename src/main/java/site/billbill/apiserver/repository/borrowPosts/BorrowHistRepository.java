package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.LocalDate;

public interface BorrowHistRepository extends JpaRepository<BorrowHistJpaEntity,Long> {
    BorrowHistJpaEntity findBorrowHistByBorrower(UserJpaEntity borrower);
    BorrowHistJpaEntity findBorrowHistByItemAndBorrowerAndStartedAtAndEndedAt(ItemsJpaEntity item,UserJpaEntity borrower,  LocalDate startedAt, LocalDate endedAt);
}
