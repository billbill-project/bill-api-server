package site.billbill.apiserver.api.borrowPosts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
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
        @Schema(description = "이미지", example = "[\"이미지링크\",\"이미지링크\"]")
        private List<String> images;
        @Schema(description = "카테고리", example = "camp, sports, tools")
        private String category;
        private List<NoRentalPeriod> noRental;
    }

    @Getter
    @Setter
    @Builder
    public static class NoRentalPeriod {
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Setter
    @Builder
    public static class BillAcceptRequest {
        private String postId;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Setter
    @Builder
    public static class ReviewRequest {
        @Schema(description = "평점", example = "1~5 사이로 입력해주셔야합니다. 그 외엔 오류 처리")
        private int rating;
        private String content;
    }

    @Data
    public static class PostIdRequest {
        private String postId;
    }

    @Data
    public static class BorrowHistRequest {
        private Long borrowSeq;
    }
}
