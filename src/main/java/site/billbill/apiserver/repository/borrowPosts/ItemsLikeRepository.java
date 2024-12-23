package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.post.ItemsLikeJpaEntity;
import site.billbill.apiserver.model.post.embeded.ItemsLikeId;

import java.util.Optional;

@Repository
public interface ItemsLikeRepository extends JpaRepository<ItemsLikeJpaEntity, ItemsLikeId> {
}
