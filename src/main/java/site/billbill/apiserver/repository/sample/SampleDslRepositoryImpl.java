package site.billbill.apiserver.repository.sample;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.sample.QSampleJpaEntity;
import site.billbill.apiserver.model.sample.SampleJpaEntity;

import java.util.List;

@Repository
@AllArgsConstructor
public class SampleDslRepositoryImpl implements SampleDslRepository {
    private final JPAQueryFactory query;

    @Override
    public List<SampleJpaEntity> getSamples(Pageable pageable) {
        QSampleJpaEntity qSample = QSampleJpaEntity.sampleJpaEntity;

        JPAQuery<SampleJpaEntity> qb = query.selectFrom(qSample)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return qb.fetch();
    }
}
