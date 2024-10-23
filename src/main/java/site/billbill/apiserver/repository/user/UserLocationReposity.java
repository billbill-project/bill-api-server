package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.user.UserLocationJpaEntity;

import java.util.Optional;

public interface UserLocationReposity extends JpaRepository<UserLocationJpaEntity, String> {
    public Optional<UserLocationJpaEntity> findByUserId(String userId);
}
