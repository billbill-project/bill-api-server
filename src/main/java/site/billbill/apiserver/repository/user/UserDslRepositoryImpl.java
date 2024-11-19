package site.billbill.apiserver.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.user.QUserJpaEntity;

import java.time.OffsetDateTime;

@Repository
@RequiredArgsConstructor
public class UserDslRepositoryImpl implements UserDslRepository {
    private final JPAQueryFactory query;

    @Override
    public void withdrawUserById(String userId) {
        QUserJpaEntity qUser = QUserJpaEntity.userJpaEntity;

        query.update(qUser)
                .where(qUser.userId.eq(userId))
                .set(qUser.withdrawStatus, true)
                .set(qUser.withdrawAt, OffsetDateTime.now())
                .set(qUser.updatedAt, OffsetDateTime.now())
                .execute();
    }
}
