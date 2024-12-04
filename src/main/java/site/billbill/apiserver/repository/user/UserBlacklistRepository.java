package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.user.UserBlacklistJpaEntity;

import java.util.Optional;

@Repository
public interface UserBlacklistRepository extends JpaRepository<UserBlacklistJpaEntity, Long>, UserBlacklistDslRepository {
    Optional<UserBlacklistJpaEntity> findByUserIdAndBlackedId(String userId, String blackedId);
}
