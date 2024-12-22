package site.billbill.apiserver.repository.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.api.users.dto.request.ProfileRequest;
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

    @Override
    public void updateProfileById(String userId, ProfileRequest request) {
        QUserJpaEntity qUser = QUserJpaEntity.userJpaEntity;

        JPAUpdateClause qb = query.update(qUser)
                .where(qUser.userId.eq(userId))
                .set(qUser.updatedAt, OffsetDateTime.now());

        if (request.getNickname() != null) qb.set(qUser.nickname, request.getNickname());
        if (request.getProfileUrl() != null) qb.set(qUser.profile, request.getProfileUrl());

        qb.execute();
    }
}
