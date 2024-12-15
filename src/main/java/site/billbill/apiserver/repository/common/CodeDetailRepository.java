package site.billbill.apiserver.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.common.CodeDetailJpaEntity;
import site.billbill.apiserver.model.common.embedded.CodeDetailId;

import java.util.List;

@Repository
public interface CodeDetailRepository extends JpaRepository<CodeDetailJpaEntity, CodeDetailId> {
    List<CodeDetailJpaEntity> findByIdGroupCode(String groupCode);
}
