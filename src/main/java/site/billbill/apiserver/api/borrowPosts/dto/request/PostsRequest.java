package site.billbill.apiserver.api.borrowPosts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import site.billbill.apiserver.common.enums.items.PriceStandard;
import site.billbill.apiserver.common.validation.EnumValidator;

import java.time.LocalDate;
import java.util.List;

public class PostsRequest {
    @Getter
    @Setter
    public static class UploadRequest {
        @Schema(description = "게시물 제목", example = "게시물 제목")
        private String title;
        @Schema(description = "가격", example = "10000")
        private int price;
        @Schema(description = "가격기준", example = "DAY,MONTH,YEAR 중 하나")
        @EnumValidator(enumClass = PriceStandard.class, message = "priceStandard 값은 DAY, MONTH, YEAR 중 하나여야 합니다.")
        private PriceStandard priceStandard;
        @Schema(description = "보증금", example = "50000")
        private int deposit;
        @Schema(description = "물품 상태", example = "1: 상, 2: 중상, 3: 중, 4: 중하, 5: 하")
        private int itemStatus;
        @Schema(description = "게시물 내용", example = "게시물 입니다.")
        private String content;
        @Schema(description = "이미지링크들", example = "{ //대여 불가능 날짜, \n" +
                "\t\t\"startDate\": 2024-11-19,\n" +
                "\t\t\"endDate\": 2024-11-23}")
        private List<String> images;
        @Schema(description = "대여불가기간", example = "[\"이미지링크\",\"이미지링크\"]")
        private List<NoRentalPeriod> noRental;

    }
    @Getter
    @Setter
    public static class NoRentalPeriod{
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
