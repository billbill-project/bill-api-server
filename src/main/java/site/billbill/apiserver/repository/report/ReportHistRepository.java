package site.billbill.apiserver.repository.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.billbill.apiserver.model.report.ReportHistJpaEntity;

@Repository
public interface ReportHistRepository extends JpaRepository<ReportHistJpaEntity, Long> {
}
