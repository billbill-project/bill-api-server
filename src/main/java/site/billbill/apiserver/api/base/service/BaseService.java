package site.billbill.apiserver.api.base.service;

import site.billbill.apiserver.api.base.dto.request.ReportRequest;
import site.billbill.apiserver.common.enums.base.ReportType;

public interface BaseService {
    void report(ReportType type, ReportRequest request);
}
