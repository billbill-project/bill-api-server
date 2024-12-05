package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

public interface ItemsBorrowRepository extends JpaRepository<ItemsBorrowJpaEntity,String> {
    ItemsBorrowJpaEntity findByItem(ItemsJpaEntity item);
}
