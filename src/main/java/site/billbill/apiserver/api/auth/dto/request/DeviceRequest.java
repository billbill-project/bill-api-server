package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import site.billbill.apiserver.common.enums.user.DeviceType;

@Data
public class DeviceRequest {
    @Schema(description = "디바이스 토큰", example = "device token")
    private String deviceToken;
    @Schema(description = "디바이스 종류", example = "IOS / ANDROID")
    private DeviceType deviceType;
    @Schema(description = "앱 버전", example = "1.0.0")
    private String appVersion;
}
