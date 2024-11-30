package site.billbill.apiserver.api.borrowPosts.converter;

import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.ItemsBorrowStatusJpaEntity;
import site.billbill.apiserver.model.post.ItemsCategoryJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

public class PostsConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static PostsResponse.UploadResponse toUploadResponse(String id){
        return PostsResponse.UploadResponse.builder().
                postId(id).
                build();
    }
    public static ItemsJpaEntity toItem(String postId, PostsRequest.UploadRequest request, UserJpaEntity user, ItemsCategoryJpaEntity category){
        return ItemsJpaEntity.builder()
                .id(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .delYn(false)
                .owner(user)
                .viewCount(0)
                .images(request.getImages())
                .category(category)
                .itemStatus(request.getItemStatus()).build();
    }
    public static ItemsBorrowJpaEntity toItemBorrow(ItemsJpaEntity item, PostsRequest.UploadRequest request){
        return ItemsBorrowJpaEntity.builder()
                .item(item)
                .priceStandard(request.getPriceStandard())
                .price(request.getPrice())
                .deposit(request.getDeposit())
                .build();
    }
    public static ItemsBorrowStatusJpaEntity toItemBorrowStatus(ItemsJpaEntity item, String status, PostsRequest.NoRentalPeriod noRentalPeriod){
        return ItemsBorrowStatusJpaEntity.builder().startDate(noRentalPeriod.getStartDate())
                .endDate(noRentalPeriod.getEndDate())
                .borrowStatusCode(status)
                .item(item)
                .build();
    }

    public static PostsResponse.Post toPost(ItemsJpaEntity item,ItemsBorrowJpaEntity borrowItem){
        return PostsResponse.Post.builder()
                .postId(item.getId())
                .image(Optional.ofNullable(item.getImages())
                        .filter(images -> !images.isEmpty())
                        .map(images -> images.get(0))
                        .orElse(null))
                .price(borrowItem.getPrice())
                .userId(item.getOwner().getUserId())
                .userName(item.getOwner().getNickname())
                .createdAt(item.getCreatedAt().format(DATE_TIME_FORMATTER))
                .likeCount(item.getLikeCount())
                .build();
    }
    public static PostsResponse.ViewAllResultResponse toViewAllList(List<PostsResponse.Post> posts){
        return PostsResponse.ViewAllResultResponse.builder().result(posts).build();
    }
    public static PostsResponse.ViewPostResponse toViewPost(ItemsJpaEntity item, ItemsBorrowJpaEntity borrowItem, List<PostsResponse.NoRentalPeriodResponse> noRental, String status){
        return PostsResponse.ViewPostResponse.builder()
                .postId(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .images(item.getImages())
                .price(borrowItem.getPrice())
                .priceStandard(borrowItem.getPriceStandard())
                .deposit(borrowItem.getDeposit())
                .noRentalPeriod(noRental)
                .itemStatus(status)
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .build();
    }
    public static PostsResponse.NoRentalPeriodResponse toNoRentalPeriod(ItemsBorrowStatusJpaEntity borrowStatus){
        return PostsResponse.NoRentalPeriodResponse.builder()
                .startDate(borrowStatus.getStartDate().format(DATE_FORMATTER))
                .endDate(borrowStatus.getEndDate().format(DATE_FORMATTER))
                .build();
    }

}
