package site.billbill.apiserver.repository.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.api.users.dto.response.BlacklistResponse;
import site.billbill.apiserver.model.user.QUserBlacklistJpaEntity;
import site.billbill.apiserver.model.user.QUserJpaEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserBlacklistDslRepositoryImpl implements UserBlacklistDslRepository {
    private final JPAQueryFactory query;


    @Override
    public List<BlacklistResponse> getBlacklistByUserId(String userId, Pageable pageable) {
        QUserBlacklistJpaEntity qUserBlacklist = QUserBlacklistJpaEntity.userBlacklistJpaEntity;
        QUserJpaEntity qUser = QUserJpaEntity.userJpaEntity;

        JPAQuery<BlacklistResponse> qb = query.select(
                        Projections.constructor(
                                BlacklistResponse.class,
                                qUserBlacklist.blacklistSeq,
                                qUserBlacklist.blackedId,
                                qUser.profile,
                                qUser.nickname,
                                qUserBlacklist.createdAt,
                                qUserBlacklist.updatedAt
                        )
                )
                .from(qUserBlacklist)
                .join(qUser).on(qUserBlacklist.blackedId.eq(qUser.userId))
                .where(qUserBlacklist.userId.eq(userId)
                        .and(qUserBlacklist.delYn.isFalse()));

        return qb.fetch();
    }
}
