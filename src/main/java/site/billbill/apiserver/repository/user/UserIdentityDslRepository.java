package site.billbill.apiserver.repository.user;

import site.billbill.apiserver.model.user.UserIdentityJpaEntity;

import java.util.Optional;

public interface UserIdentityDslRepository {
    public Optional<UserIdentityJpaEntity> findUserByPhoneNumberWithoutWithdraw(String phoneNumber);
}
