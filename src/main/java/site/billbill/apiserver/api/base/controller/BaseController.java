package site.billbill.apiserver.api.base.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.api.base.dto.request.ReportRequest;
import site.billbill.apiserver.api.base.service.BaseService;
import site.billbill.apiserver.common.enums.base.ReportType;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;
import site.billbill.apiserver.model.common.CodeDetailJpaEntity;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Base", description = "Base API")
@RequestMapping("/api/v1/base")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "404", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
})
public class BaseController {
    private final BaseService baseService;

    @Operation(summary = "신고하기 코드 목록 조회", description = "신고하기 코드 목록 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/report/code")
    public BaseResponse<List<CodeDetailJpaEntity>> getReportCodeList() {
        return new BaseResponse<>(baseService.getReportCodeList());
    }

    @Operation(summary = "신고하기", description = "신고하기 API")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/report/{type}")
    public BaseResponse<String> report(@PathVariable ReportType type, @RequestBody ReportRequest request) {
        baseService.report(type, request);
        return new BaseResponse<>(null);
    }
}
