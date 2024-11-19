package site.billbill.apiserver.api.borrowPosts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import site.billbill.apiserver.api.borrowPosts.controller.PostsController;
import site.billbill.apiserver.api.borrowPosts.converter.PostsConverter;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.common.utils.ULID.ULIDUtil;
import site.billbill.apiserver.model.post.ItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.ItemsBorrowStatusJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.ItemsBorrowRepository;
import site.billbill.apiserver.repository.borrowPosts.ItemsBorrowStatusRepository;
import site.billbill.apiserver.repository.borrowPosts.ItemsRepository;
import site.billbill.apiserver.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor

@Slf4j

public class PostsServiceImpl implements PostsService {

    private final UserRepository userRepository;
    private final ItemsRepository itemsRepository;
    private final ItemsBorrowRepository itemsBorrowRepository;
    private final ItemsBorrowStatusRepository itemsBorrowStatusRepository;
    public PostsResponse.UploadResponse uploadPostService(PostsRequest.UploadRequest request,String userId){
        //먼저 item 생성,
        Optional<UserJpaEntity> isUser=userRepository.findById(userId);
        String postsId = ULIDUtil.generatorULID("BORROW");

        UserJpaEntity user=new UserJpaEntity();
        if(isUser.isPresent()){
            user=isUser.get();
        }
        //Item 생성
        ItemsJpaEntity newItem= PostsConverter.toItem(postsId,request,user);
        itemsRepository.save(newItem);
        ItemsJpaEntity item=itemsRepository.findById(postsId).orElse(newItem);
        //BorrowItem 생성
        ItemsBorrowJpaEntity newBorrowItem= PostsConverter.toItemBorrow(item,request);
        itemsBorrowRepository.save(newBorrowItem);
        //대여 불가 기간 생성
        if (request.getNoRental() != null && !request.getNoRental().isEmpty()) {
        List<ItemsBorrowStatusJpaEntity> itemsBorrowStatusList = request.getNoRental().stream()
                .map(status -> PostsConverter.toItemBorrowStatus(item, "RENTAL_NOT_POSSIBLE", status))
                .toList();
        itemsBorrowStatusRepository.saveAll(itemsBorrowStatusList);
        }



        return PostsConverter.toUploadResponse(postsId);





    }
}
