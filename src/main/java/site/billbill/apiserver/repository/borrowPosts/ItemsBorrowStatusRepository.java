package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsBorrowStatusJpaEntity;

import java.util.List;

public interface ItemsBorrowStatusRepository extends JpaRepository<ItemsBorrowStatusJpaEntity,Long> {
    List<ItemsBorrowStatusJpaEntity> findAllByItemIdAndBorrowStatusCode(String itemId,String borrowStatusCode);
    List<ItemsBorrowStatusJpaEntity> findAllByItemIdAndBorrowStatusCodeIn(String itemId, List<String> statusCodes);
}
