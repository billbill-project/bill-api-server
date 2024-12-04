package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.user.UserSearchHistJpaEntity;


public interface UserSearchHistRepository extends JpaRepository<UserSearchHistJpaEntity, String> {
}
