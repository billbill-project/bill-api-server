package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

public interface ItemDslRepository {
    Page<ItemsJpaEntity> findItemsWithConditions(String category, Pageable pageable, String sortField);
}
