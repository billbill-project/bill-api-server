package site.billbill.apiserver.api.users.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class BorrowHistoryResponse {
    @Schema(description = "대여 기록 번호", example = "2")
    private Long borrowSeq;
    @Schema(description = "물품 ID", example = "ITEM-XXXXX...")
    private String itemId;
    @Schema(description = "대여자 ID", example = "USER-XXXXX...")
    private String borrowerId;
    @Enumerated(EnumType.STRING)
    @Schema(description = "물품 타입", example = "BORROWING / BORROWED / EXCHANGE")
    private ItemHistoryType type;
    @Schema(
            description = "물품 이미지 리스트",
            type = "array",
            example = "[\"image1.jpg\", \"image2.jpg\"]"
    )
    private List<String> itemImages;
    @Schema(description = "물건 가격", type = "int", example = "18000")
    private Integer price;
    @Schema(description = "물품 이름", example = "Pronto600 폴라로이드 카메라")
    private String title;
    @Schema(description = "대여 시작 일자", example = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;
    @Schema(description = "대여 종료 일자", example = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;
    @Schema(description = "아이템 상태", example = "3")
    private Integer itemStatus;
    @Schema(description = "좋아요 수", example = "12")
    private Long likeCount;
    @Schema(description = "채팅 수", example = "1")
    private Long chatCount;
    @Schema(description = "조회 수", example = "12")
    private Integer viewCount;
    @Schema(description = "생성일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime createdAt;
    @Schema(description = "수정일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime updatedAt;
}
