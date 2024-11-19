package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

@Repository
public interface ItemsRepository extends JpaRepository<ItemsJpaEntity,String> {
}
