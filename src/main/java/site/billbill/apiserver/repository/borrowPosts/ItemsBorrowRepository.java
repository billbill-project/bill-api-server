package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;

public interface ItemsBorrowRepository extends JpaRepository<ItemsBorrowJpaEntity,String> {
}
