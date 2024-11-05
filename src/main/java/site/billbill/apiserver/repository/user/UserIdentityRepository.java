package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.user.UserIdentityJpaEntity;

import java.util.Optional;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentityJpaEntity, String>, UserIdentityDslRepository {
    boolean existsByNameAndPhoneNumber(String name, String phoneNumber);

    Optional<UserIdentityJpaEntity> findByPhoneNumber(String phoneNumber);
}
