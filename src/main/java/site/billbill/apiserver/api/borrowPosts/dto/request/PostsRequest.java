package site.billbill.apiserver.api.borrowPosts.dto.request;

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
        private String title;
        private int price;
        @EnumValidator(enumClass = PriceStandard.class, message = "priceStandard 값은 DAY, MONTH, YEAR 중 하나여야 합니다.")
        private PriceStandard priceStandard;
        private int deposit;
        private int itemStatus;
        private String content;
        private List<String> images;
        private List<NoRentalPeriod> noRental;

    }
    @Getter
    @Setter
    public static class NoRentalPeriod{
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
