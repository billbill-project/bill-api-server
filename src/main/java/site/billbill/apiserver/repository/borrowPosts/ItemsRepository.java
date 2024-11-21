package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemsRepository extends JpaRepository<ItemsJpaEntity,String> ,ItemDslRepository{
}
