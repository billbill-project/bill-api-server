package site.billbill.apiserver.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.auth.dto.request.SignupRequest;
import site.billbill.apiserver.api.auth.service.AuthService;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;

@Slf4j
@RestController
@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
})
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원 가입(일반)", description = "일반 회원 가입 API")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public BaseResponse<JwtDto> signup(
            @RequestBody SignupRequest request
    ) {
        JwtDto data = authService.signup(request);
        return new BaseResponse<>(data);
    }
}
