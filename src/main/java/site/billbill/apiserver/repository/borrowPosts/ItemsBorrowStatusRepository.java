package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsBorrowStatusJpaEntity;

public interface ItemsBorrowStatusRepository extends JpaRepository<ItemsBorrowStatusJpaEntity,Long> {
}
