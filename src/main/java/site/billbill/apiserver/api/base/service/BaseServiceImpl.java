package site.billbill.apiserver.api.base.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.base.dto.request.ReportRequest;
import site.billbill.apiserver.common.enums.base.ReportStatus;
import site.billbill.apiserver.common.enums.base.ReportType;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.model.common.CodeDetailJpaEntity;
import site.billbill.apiserver.model.report.ReportHistJpaEntity;
import site.billbill.apiserver.repository.common.CodeDetailRepository;
import site.billbill.apiserver.repository.report.ReportHistRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseServiceImpl implements BaseService {
    private final ReportHistRepository reportHistRepository;
    private final CodeDetailRepository codeDetailRepository;

    @Override
    public List<CodeDetailJpaEntity> getReportCodeList() {
        return codeDetailRepository.findByIdGroupCodeOrderBySeqAsc("REPORT_CODE");
    }

    @Override
    @Transactional
    public void report(ReportType type, ReportRequest request) {
        String currentUserId = MDC.get(JWTUtil.MDC_USER_ID);

        ReportHistJpaEntity reportHistJpaEntity = ReportHistJpaEntity.builder()
                .reportSeq(null)
                .reportType(type)
                .targetId(request.getUserId())
                .reporterId(currentUserId)
                .reportCode(request.getReportCode())
                .description(request.getDescription())
                .reportStatus(ReportStatus.REPORTED)
                .build();

        reportHistRepository.save(reportHistJpaEntity);
    }
}
