package site.billbill.apiserver.repository.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.user.QUserIdentityJpaEntity;
import site.billbill.apiserver.model.user.QUserJpaEntity;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserIdentityDslRepositoryImpl implements UserIdentityDslRepository {
    private final JPAQueryFactory query;


    @Override
    public Optional<UserIdentityJpaEntity> findUserByPhoneNumberWithoutWithdraw(String phoneNumber) {
        QUserIdentityJpaEntity qUserIdentity = QUserIdentityJpaEntity.userIdentityJpaEntity;
        QUserJpaEntity qUser = QUserJpaEntity.userJpaEntity;

        JPAQuery<UserIdentityJpaEntity> qb = query.selectFrom(qUserIdentity)
                .join(qUser).on(qUserIdentity.userId.eq(qUser.userId))
                .where(qUserIdentity.phoneNumber.eq(phoneNumber))
                .where(qUser.withdrawStatus.isFalse());


        return Optional.ofNullable(qb.fetchOne());
    }
}
