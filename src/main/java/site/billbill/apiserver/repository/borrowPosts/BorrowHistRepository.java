package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.BorrowHistJapEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

public interface BorrowHistRepository extends JpaRepository<BorrowHistJapEntity,Long> {
    BorrowHistJapEntity findBorrowHistByBorrower(UserJpaEntity borrower);
}
