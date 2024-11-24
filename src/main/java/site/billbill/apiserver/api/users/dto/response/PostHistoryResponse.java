package site.billbill.apiserver.api.users.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import site.billbill.apiserver.common.utils.posts.ItemType;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PostHistoryResponse {
    @Schema(description = "물품 ID", example = "ITEM-XXXXX...")
    private String itemId;
    //    @Enumerated(EnumType.STRING)
//    @Schema(description = "물품 타입", example = "BORROW")
//    private ItemType type;
    @Schema(
            description = "물품 이미지 리스트",
            type = "array",
            example = "[\"image1.jpg\", \"image2.jpg\"]"
    )
    private List<String> itemImages;
    @Schema(description = "물품 이름", example = "Pronto600 폴라로이드 카메라")
    private String title;
    @Schema(description = "아이템 상태", example = "3")
    private int itemStatus;
    @Schema(description = "좋아요 수", example = "12")
    private long likeCount;
    @Schema(description = "채팅 수", example = "1")
    private long chatCount;
    @Schema(description = "조회 수", example = "12")
    private int viewCount;
    @Schema(description = "생성일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime createdAt;
    @Schema(description = "수정일시", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private OffsetDateTime updatedAt;
}
