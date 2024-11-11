package site.billbill.apiserver.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.auth.dto.request.*;
import site.billbill.apiserver.api.auth.service.AuthService;
import site.billbill.apiserver.api.auth.service.OAuthService;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.dto.JwtDto;

@Slf4j
@RestController
@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "404", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "409", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
})
public class AuthController {

    private final AuthService authService;
    private final OAuthService oAuthService;

    @Operation(summary = "회원 가입(일반)", description = "일반 회원 가입 API")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public BaseResponse<JwtDto> signup(@RequestBody SignupRequest request) {
        return new BaseResponse<>(authService.signup(request));
    }

    @Operation(summary = "로그인(일반)", description = "일반 로그인 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public BaseResponse<JwtDto> login(@RequestBody LoginRequest request) {
        return new BaseResponse<>(authService.login(request));
    }

    @Operation(summary = "토큰 재발급", description = "리프레쉬 토큰 기반 액세스 토큰을 재발급 받는 API")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/reissue")
    public BaseResponse<JwtDto> reissue(@RequestBody ReissueRequest request) {
        return new BaseResponse<>(authService.reissue(request.getRefreshToken()));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/oauth2/code/kakao/callback")
    public BaseResponse<JwtDto> kakaoCallback(@RequestParam("code") String code) {
        return new BaseResponse<JwtDto>(oAuthService.kakaoCallback(code));
    }

//    @Operation(summary = "휴대폰 본인인증", description = "PASS NICE 본인인증 API")
//    @ResponseStatus(HttpStatus.OK)
//    @PostMapping("/identity")
//    public BaseResponse<JwtDto> identity(@RequestBody IdentityRequest request) {
//        return null;
//    }
}
