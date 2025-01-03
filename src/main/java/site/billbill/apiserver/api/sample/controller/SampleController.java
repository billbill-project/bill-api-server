package site.billbill.apiserver.api.sample.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.sample.dto.request.SamplePostRequest;
import site.billbill.apiserver.api.sample.dto.response.SampleResponse;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.sample.SampleJpaEntity;
import site.billbill.apiserver.repository.sample.SampleRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sample")
public class SampleController {
    private final SampleRepository sampleRepository;
    //주석
    @PostMapping("")
    public BaseResponse postSample(
            @RequestBody SamplePostRequest request
    ) {
        SampleJpaEntity entity = new SampleJpaEntity();
        entity.setId(ULIDUtil.generatorULID("USER"));
        entity.setTextList(request.getTextList());
        entity.setYn(true);

        SampleJpaEntity saved = sampleRepository.save(entity);

        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("샘플 저장 성공");
        response.setData(saved);
        return response;
    }

    @GetMapping("")
    public BaseResponse getSample(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of((page < 1 ? 0 : page - 1), size);

        List<SampleJpaEntity> entity = sampleRepository.getSamples(pageable);

        if(page == 3) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(true);
            response.setMessage("샘플 호출 성공");
            response.setData(new SampleResponse(OffsetDateTime.now()));
            return response;
        }

        if(entity.isEmpty()) {
            throw new CustomException(ErrorCode.BadRequest, "잘못 요청했습니다.", HttpStatus.BAD_REQUEST);
        }

        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("샘플 호출 성공");
        response.setData(entity);
        return response;
    }
}
