package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserJpaEntity, String>, UserDslRepository {
    Optional<UserJpaEntity> findByUserIdAndWithdrawStatus(String userId, boolean withdrawStatus);
    Optional<UserJpaEntity> findByProviderId(String providerId);
    boolean existsByNicknameAndWithdrawStatusFalse(String nickname);
    boolean existsByEmailAndWithdrawStatusFalse(String email);
}
