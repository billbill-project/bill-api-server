package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.post.ItemsLocationJpaEntity;

public interface ItemsLocationRepository extends JpaRepository<ItemsLocationJpaEntity,String> {
    ItemsLocationJpaEntity findByItem(ItemsJpaEntity item);
}
