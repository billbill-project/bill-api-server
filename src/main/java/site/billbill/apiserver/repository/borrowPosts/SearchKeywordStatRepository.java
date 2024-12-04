package site.billbill.apiserver.repository.borrowPosts;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.post.SearchKeywordStatsJpaEntity;

public interface SearchKeywordStatRepository extends JpaRepository<SearchKeywordStatsJpaEntity, Long> {
    SearchKeywordStatsJpaEntity findByKeyword(String keyword);
}
