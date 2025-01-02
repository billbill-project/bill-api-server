package site.billbill.apiserver.api.base.service;

import site.billbill.apiserver.api.base.dto.request.ReportRequest;
import site.billbill.apiserver.common.enums.base.ReportType;
import site.billbill.apiserver.model.common.CodeDetailJpaEntity;

import java.util.List;

public interface BaseService {
    List<CodeDetailJpaEntity> getReportCodeList();

    void report(ReportType type, ReportRequest request);
}
