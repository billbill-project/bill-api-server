package site.billbill.apiserver.repository.user;

import site.billbill.apiserver.api.users.dto.request.ProfileRequest;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.util.Optional;

public interface UserDslRepository {
    void withdrawUserById(String userId);

    void updateProfileById(String userId, ProfileRequest request);

    Optional<UserJpaEntity> findByEmailWithoutWithdraw(String email);
}
