package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.post.ItemsReviewJpaEntity;

import java.util.List;

public interface ItemsReivewRepository extends JpaRepository<ItemsReviewJpaEntity,String> {
    List<ItemsReviewJpaEntity> findAllByItemsOrderByCreatedAtDesc(ItemsJpaEntity item);


}
