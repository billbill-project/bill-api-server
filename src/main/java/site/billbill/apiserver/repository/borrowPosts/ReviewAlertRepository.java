package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ReviewAlertJpaEntity;

import java.util.List;

public interface ReviewAlertRepository extends JpaRepository<ReviewAlertJpaEntity,Long> {
    ReviewAlertJpaEntity findOneByBorrowHistAndType(BorrowHistJpaEntity borrowHist,String type);
    List<ReviewAlertJpaEntity> findAllByStatusAndType(String status,String type);
}
