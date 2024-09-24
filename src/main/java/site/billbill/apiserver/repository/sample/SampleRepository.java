package site.billbill.apiserver.repository.sample;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.sample.SampleJpaEntity;

@Repository
public interface SampleRepository extends JpaRepository<SampleJpaEntity, String>, SampleDslRepository {
}
