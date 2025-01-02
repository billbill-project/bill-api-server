package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.model.user.UserSearchHistJpaEntity;

import java.util.List;


public interface UserSearchHistRepository extends JpaRepository<UserSearchHistJpaEntity, String> {
    List<UserSearchHistJpaEntity> findByUserAndDelYnOrderByCreatedAtDesc(UserJpaEntity user,boolean delYn);
}
