package site.billbill.apiserver.api.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billbill.apiserver.api.users.dto.response.ProfileResponse;
import site.billbill.apiserver.api.users.service.UserService;
import site.billbill.apiserver.common.response.BaseResponse;

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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/profile")
    public BaseResponse<ProfileResponse> profile() {
        return new BaseResponse<ProfileResponse>(userService.getProfile());
    }
}
