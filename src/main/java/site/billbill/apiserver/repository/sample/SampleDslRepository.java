package site.billbill.apiserver.repository.sample;

import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.model.sample.SampleJpaEntity;

import java.util.List;

public interface SampleDslRepository {
    List<SampleJpaEntity> getSamples(Pageable pageable);
}
