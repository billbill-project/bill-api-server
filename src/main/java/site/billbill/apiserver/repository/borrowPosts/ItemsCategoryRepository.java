package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsCategoryJpaEntity;

public interface ItemsCategoryRepository extends JpaRepository<ItemsCategoryJpaEntity,String> {
    ItemsCategoryJpaEntity findByName(String name);
}
