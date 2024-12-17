package site.billbill.apiserver.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.user.WithdrawHistJpaEntity;

@Repository
public interface WithdrawHistRepository extends JpaRepository<WithdrawHistJpaEntity, Long> {
}
