package site.billbill.apiserver.api.borrowPosts.converter;

import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.common.enums.items.PriceStandard;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.ItemsBorrowStatusJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.ItemsBorrowStatusRepository;

public class PostsConverter {

    public static PostsResponse.UploadResponse toUploadResponse(String id){
        return PostsResponse.UploadResponse.builder().
                postId(id).
                build();
    }
    public static ItemsJpaEntity toItem(String postId,PostsRequest.UploadRequest request, UserJpaEntity user){
        return ItemsJpaEntity.builder()
                .id(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .delYn(false)
                .owner(user)
                .viewCount(0)
                .images(request.getImages())
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

}
