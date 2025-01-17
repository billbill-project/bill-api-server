package site.billbill.apiserver.api.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.auth.dto.request.DeviceRequest;
import site.billbill.apiserver.api.auth.dto.request.LocationRequest;
import site.billbill.apiserver.api.users.dto.request.*;
import site.billbill.apiserver.api.users.dto.response.*;
import site.billbill.apiserver.api.users.service.UserService;
import site.billbill.apiserver.common.response.BaseResponse;
import site.billbill.apiserver.common.utils.jwt.JWTUtil;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;
import site.billbill.apiserver.model.common.CodeDetailJpaEntity;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Users", description = "Users API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "404", description = "Server Error", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
})
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "내 프로필 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile")
    public BaseResponse<ProfileResponse> profile() {
        return new BaseResponse<>(userService.getProfileInfo());
    }

    @Operation(summary = "타 회원 프로필 조회", description = "타 회원 프로필 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile/{userId}")
    public BaseResponse<ProfileResponse> profile(@PathVariable String userId) {
        return new BaseResponse<>(userService.getProfileInfo(userId));
    }

    @Operation(summary = "회원 차단하기", description = "회원 차단 API")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/blacklist")
    public BaseResponse<String> blackUser(@RequestBody BlacklistRequest request) {
        userService.blockUser(request.getUserId());
        return new BaseResponse<>(null);
    }

    @Operation(summary = "내 차단목록 조회", description = "회원 본인의 차단 목록을 조회하는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/blacklist")
    public BaseResponse<List<BlacklistResponse>> blacklist(
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of((page < 1 ? 0 : page - 1), size);
        return new BaseResponse<>(userService.getBlacklist(pageable));
    }

    @Operation(summary = "차단 취소", description = "차단을 취소하는 API")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/blacklist")
    public BaseResponse<String> blacklist(@RequestBody BlacklistRequest request) {
        userService.blockCancel(request.getUserId());
        return new BaseResponse<>(null);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/withdraw")
    public BaseResponse<String> withdraw(@RequestBody WithdrawRequest request) {
        userService.withdraw(request);
        return new BaseResponse<>(null);
    }

    @Operation(summary = "내 글 조회(대여 내역)", description = "대여중인 글 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/history/posts")
    public BaseResponse<List<PostHistoryResponse>> postsHistory(
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        String userId = MDC.get(JWTUtil.MDC_USER_ID);
        Pageable pageable = PageRequest.of((page < 1 ? 0 : page - 1), size);
        return new BaseResponse<>(userService.getPostHistory(userId, pageable));
    }

    @Operation(summary = "타 회원 대여 내역 조회", description = "타 회원의 대여 내역을 조회하는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/history/posts/{userId}")
    public BaseResponse<List<PostHistoryResponse>> postsHistoryOthers(
            @PathVariable String userId,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of((page < 1 ? 0 : page - 1), size);
        return new BaseResponse<>(userService.getPostHistory(userId, pageable));
    }

    @Operation(summary = "내 대여기록 조회", description = "내 대여기록 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/history/{type}")
    public BaseResponse<List<BorrowHistoryResponse>> postsHistory(
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @PathVariable ItemHistoryType type
    ) {
        Pageable pageable = PageRequest.of((page < 1 ? 0 : page - 1), size);
        return new BaseResponse<>(userService.getPostHistory(pageable, type));
    }

    @Operation(summary = "내 위시리스트 조회", description = "내 위시리스트 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/wishlists")
    public BaseResponse<List<WishlistResponse>> getWishlists(
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of((page < 1 ? 0 : page - 1), size);
        return new BaseResponse<>(userService.getWishlists(pageable));
    }

    @Operation(summary = "내 디바이스 업데이트", description = "내 디바이스 정보를 업데이트하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/device")
    public BaseResponse<String> updateDevice(@RequestBody DeviceRequest request) {
        userService.updateDevice(request);
        return new BaseResponse<>(null);
    }

    @Operation(summary = "내 위치 업데이트", description = "내 위치 정보를 업데이트하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/location")
    public BaseResponse<String> updateLocation(@RequestBody LocationRequest request) {
        userService.saveLocation(null, request);
        return new BaseResponse<>(null);
    }

    @Operation(summary = "비밀번호 확인", description = "비밀번호를 확인하는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/password-check")
    public BaseResponse<Boolean> checkPassword(@RequestParam(name = "password") String password) {
        return new BaseResponse<>(userService.checkOriginalPassword(password));
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/password")
    public BaseResponse<Void> updatePassword(@RequestBody PasswordRequest request) {
        userService.updatePassword(request);
        return new BaseResponse<>(null);
    }

    @Operation(summary = "회원 탈퇴 코드 목록 조회", description = "회원 탈퇴 코드 목록 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/withdraw/code")
    public BaseResponse<List<CodeDetailJpaEntity>> getWithdrawCodeList() {
        return new BaseResponse<>(userService.getWithdrawCodeList());
    }

    @Operation(summary = "회원 프로필 수정", description = "내 프로필을 수정하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/profile")
    public BaseResponse<Void> updateProfile(@RequestBody ProfileRequest request) {
        userService.updateProfile(request);
        return new BaseResponse<>(null);
    }

    @Operation(summary = "선택 동의항목 수정", description = "선택 동의항목들의 동의여부를 수정하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/agreement")
    public BaseResponse<Void> updateAgreement(@RequestBody AgreeRequest request) {
        userService.updateAgreement(request);
        return new BaseResponse<>(null);
    }
}
