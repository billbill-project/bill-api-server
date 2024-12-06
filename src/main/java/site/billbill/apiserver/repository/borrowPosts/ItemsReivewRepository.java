package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.ItemsReviewJpaEntity;

public interface ItemsReivewRepository extends JpaRepository<ItemsReviewJpaEntity,String> {

}
