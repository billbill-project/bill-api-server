package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

public interface BorrowHistRepository extends JpaRepository<BorrowHistJpaEntity,Long> {
    BorrowHistJpaEntity findBorrowHistByBorrower(UserJpaEntity borrower);
}
