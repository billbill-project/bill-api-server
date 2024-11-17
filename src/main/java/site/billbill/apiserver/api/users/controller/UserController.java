package site.billbill.apiserver.api.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.users.dto.request.BlacklistRequest;
import site.billbill.apiserver.api.users.dto.response.BlacklistResponse;
import site.billbill.apiserver.api.users.dto.response.ProfileResponse;
import site.billbill.apiserver.api.users.service.UserService;
import site.billbill.apiserver.common.response.BaseResponse;

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
        return new BaseResponse<ProfileResponse>(userService.getProfileInfo());
    }

    @Operation(summary = "타 회원 프로필 조회", description = "타 회원 프로필 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/profile/{userId}")
    public BaseResponse<ProfileResponse> profile(@PathVariable String userId) {
        return new BaseResponse<ProfileResponse>(userService.getProfileInfo(userId));
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
}
