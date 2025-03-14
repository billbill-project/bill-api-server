package site.billbill.apiserver.api.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationResponse {
    @Schema(description = "주소", example = "서울특별시 강남구")
    private String address;
    @Schema(description = "위도", example = "127.423084873712")
    private double latitude;
    @Schema(description = "경도", example = "37.0789561558879")
    private double longitude;
}
