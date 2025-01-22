package site.billbill.apiserver.repository.borrowPosts;

import com.querydsl.core.types.OrderSpecifier;

import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.api.users.dto.response.BorrowHistoryResponse;
import site.billbill.apiserver.api.users.dto.response.PostHistoryResponse;
import site.billbill.apiserver.api.users.dto.response.WishlistResponse;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.common.utils.posts.ItemHistoryType;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.alarm.QAlarmListJpaEntity;
import site.billbill.apiserver.model.alarm.QAlarmLogJpaEntity;
import site.billbill.apiserver.model.chat.QChatChannelJpaEntity;
import site.billbill.apiserver.model.post.*;
import site.billbill.apiserver.model.user.QUserJpaEntity;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ItemDslRepositoryImpl implements ItemDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemsJpaEntity> findItemsWithConditions(String category, Pageable pageable, String sortField, String keyword, Double latitude, Double longitude) {
        QItemsJpaEntity items = QItemsJpaEntity.itemsJpaEntity;
        QItemsBorrowJpaEntity borrow = QItemsBorrowJpaEntity.itemsBorrowJpaEntity;
        QItemsCategoryJpaEntity categoryEntity = QItemsCategoryJpaEntity.itemsCategoryJpaEntity;
        QItemsLocationJpaEntity QItemsLocation = QItemsLocationJpaEntity.itemsLocationJpaEntity;

        JPAQuery<ItemsJpaEntity> query = queryFactory.selectFrom(items)
                .leftJoin(items.category, categoryEntity).fetchJoin() // 명시적 Fetch Join
                .leftJoin(borrow).on(items.id.eq(borrow.item.id))
                .where(items.delYn.isFalse());
        query.leftJoin(items.location, QItemsLocationJpaEntity.itemsLocationJpaEntity).fetchJoin();
        // 카테고리 필터링
        if (category == null) {
            query.where(items.category.isNull());
        } else if (!"entire".equals(category)) {
            var fetchedCategory = queryFactory.selectFrom(categoryEntity)
                    .where(categoryEntity.name.eq(category))
                    .fetchOne();

            if (fetchedCategory == null) {
                log.warn("카테고리를 찾을 수 없음.", category);
                return new PageImpl<>(List.of(), pageable, 0);
            }
            query.where(items.category.eq(fetchedCategory));
        }

        //키워드 필터링
        if (keyword != null && !keyword.isEmpty()) {
            query.where(applyKeywordFilter(items, keyword));
        }

        // 정렬 조건 처리
        pageable.getSort().forEach(order -> {
            OrderSpecifier<?> orderSpecifier = null;

            switch (order.getProperty()) {
                case "price" -> orderSpecifier = order.isAscending() ? borrow.price.asc() : borrow.price.desc();
                case "createdAt" ->
                        orderSpecifier = order.isAscending() ? items.createdAt.asc() : items.createdAt.desc();
                case "likeCount" ->
                        orderSpecifier = order.isAscending() ? items.likeCount.asc() : items.likeCount.desc();
                case "distance" -> {
                    try {
                        // null 체크를 먼저 수행
                        if (latitude != null && longitude != null) {
                            // 거리 계산식
                            NumberExpression<Double> distanceExpression = Expressions.numberTemplate(
                                Double.class,
                                "6371 * acos(cos(radians({0})) * cos(radians({2})) * cos(radians({3}) - radians({1})) + sin(radians({0})) * sin(radians({2})))",
                                QItemsLocation.latitude, QItemsLocation.longitude, latitude, longitude
                            );

                            orderSpecifier = order.isAscending() ? distanceExpression.asc() : distanceExpression.desc();

                        } else {
                            // latitude와 longitude가 null일 경우 createdAt 기준 정렬
                            log.info("거리 별 정렬 실패");
                            orderSpecifier = order.isAscending() ? items.createdAt.asc() : items.createdAt.desc();
                        }
                    } catch (Exception e) {
                        // 디버깅: 오류 발생 시 스택 트레이스 출력
                        log.error("Error in distance sorting expression", e);
                    }
                }
                default -> {
                    log.warn("Invalid sort field: {}", order.getProperty());
                    return;
                }
            }

            query.orderBy(orderSpecifier);
        });


        // 페이징 처리
        List<ItemsJpaEntity> content = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info("얻은 콘텐츠 사이즈: {}", content.size());

        long total = queryFactory.selectFrom(items)
                .where(items.delYn.isFalse())
                .fetchCount();

        log.info("아이템의 사이즈: {}", total);

        // 페이징 초과 방지
        if (pageable.getOffset() >= total) {
            log.warn("전체 아이템보다 요구 페이지가 많습빈다.");
            return new PageImpl<>(List.of(), pageable, total);
        }

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, Sort.Order sortOrder) {
        QItemsJpaEntity items = QItemsJpaEntity.itemsJpaEntity;
        QItemsBorrowJpaEntity borrow = QItemsBorrowJpaEntity.itemsBorrowJpaEntity;

        boolean isAscending = sortOrder == null || sortOrder.isAscending();

        switch (sortField) {
            case "price":
                return isAscending ? borrow.price.asc() : borrow.price.desc();
            case "createdAt":
                return isAscending ? items.createdAt.asc() : items.createdAt.desc();
            case "likeCount":
                return isAscending ? items.likeCount.asc() : items.likeCount.desc();
            default:
                return null; // 기본 정렬 없음
        }
    }

    private BooleanExpression applyKeywordFilter(QItemsJpaEntity items, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        // '+'를 기준으로 키워드 분리
        String[] keywords = keyword.split("\\+");

        // 키워드 조건 생성
        BooleanExpression keywordCondition = null;
        for (String key : keywords) {
            BooleanExpression condition = items.title.containsIgnoreCase(key)
                    .or(items.content.containsIgnoreCase(key));

            if (keywordCondition == null) {
                keywordCondition = condition;
            } else {
                keywordCondition = keywordCondition.or(condition); // AND 대신 OR 사용
            }
        }

        return keywordCondition;
    }

    @Override
    public List<PostHistoryResponse> getPostHistory(String userId, Pageable pageable) {
        QItemsJpaEntity qItems = QItemsJpaEntity.itemsJpaEntity;
        QItemsBorrowJpaEntity qBorrow = QItemsBorrowJpaEntity.itemsBorrowJpaEntity;
        QItemsLikeJpaEntity qLike = QItemsLikeJpaEntity.itemsLikeJpaEntity;
        QChatChannelJpaEntity qChatChannel = QChatChannelJpaEntity.chatChannelJpaEntity;

        JPAQuery<PostHistoryResponse> qb = queryFactory.select(
                        Projections.constructor(
                                PostHistoryResponse.class,
                                qItems.id,
                                qItems.images,
                                qBorrow.price,
                                qItems.title,
                                qItems.itemStatus,
                                qLike.countDistinct().as("likeCount"),
                                qChatChannel.countDistinct().as("chatCount"),
                                qItems.viewCount,
                                qItems.createdAt,
                                qItems.updatedAt
                        )
                )
                .from(qItems)
                .leftJoin(qBorrow).on(qItems.id.eq(qBorrow.id))
                .leftJoin(qLike).on(qItems.id.eq(qLike.id.itemId).and(qLike.delYn.isFalse()))
                .leftJoin(qChatChannel).on(qItems.id.eq(qChatChannel.item.id).and(qChatChannel.delYn.isFalse()))
                .where(qItems.owner.userId.eq(userId)
                        .and(qItems.delYn.isFalse()))
                .groupBy(qItems.id)  // 그룹핑 필요
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return qb.fetch();
    }

    @Override
    public List<BorrowHistoryResponse> getBorrowHistory(String userId, Pageable pageable, ItemHistoryType type) {
        QItemsJpaEntity qItems = QItemsJpaEntity.itemsJpaEntity;
        QItemsBorrowJpaEntity qBorrow = QItemsBorrowJpaEntity.itemsBorrowJpaEntity;
        QItemsLikeJpaEntity qLike = QItemsLikeJpaEntity.itemsLikeJpaEntity;
        QChatChannelJpaEntity qChatChannel = QChatChannelJpaEntity.chatChannelJpaEntity;
        QBorrowHistJpaEntity qBorrowHist = QBorrowHistJpaEntity.borrowHistJpaEntity;

        JPAQuery<BorrowHistoryResponse> qb = queryFactory.select(
                        Projections.constructor(
                                BorrowHistoryResponse.class,
                                qBorrowHist.borrowSeq,
                                qItems.id,
                                qItems.owner.userId.as("borrowerId"),
                                Expressions.constant(type),
                                qItems.images,
                                qBorrow.price,
                                qItems.title,
                                qBorrowHist.startedAt.as("startedAt"),
                                qBorrowHist.endedAt.as("endedAt"),
                                qItems.itemStatus,
                                qLike.countDistinct().as("likeCount"),
                                qChatChannel.countDistinct().as("chatCount"),
                                qItems.viewCount,
                                qItems.createdAt,
                                qItems.updatedAt
                        )
                )
                .from(qItems)
                .leftJoin(qBorrow).on(qItems.id.eq(qBorrow.id))
                .leftJoin(qLike).on(qItems.id.eq(qLike.id.itemId).and(qLike.delYn.isFalse()))
                .leftJoin(qChatChannel).on(qItems.id.eq(qChatChannel.item.id).and(qChatChannel.delYn.isFalse()))
                .rightJoin(qBorrowHist).on(qItems.id.eq(qBorrowHist.item.id).and(qBorrowHist.delYn.isFalse()))
                .where(qItems.delYn.isFalse());
        switch (type) {
            case BORROWING -> qb.where(qItems.owner.userId.eq(userId).and(qItems.id.eq(qBorrowHist.item.id)));
            case BORROWED -> qb.where(qBorrowHist.borrower.userId.eq(userId));
            case EXCHANGE -> {}
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        qb.groupBy(qItems.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return qb.fetch();
    }

    @Override
    public List<WishlistResponse> getWishlists(String userId, Pageable pageable) {
        QItemsJpaEntity qItems = QItemsJpaEntity.itemsJpaEntity;
        QItemsBorrowJpaEntity qBorrow = QItemsBorrowJpaEntity.itemsBorrowJpaEntity;
        QItemsLikeJpaEntity qLike = QItemsLikeJpaEntity.itemsLikeJpaEntity;
        QChatChannelJpaEntity qChatChannel = QChatChannelJpaEntity.chatChannelJpaEntity;

        JPAQuery<WishlistResponse> qb = queryFactory.select(
                        Projections.constructor(
                                WishlistResponse.class,
                                qItems.id,
                                qItems.owner.userId,
                                qItems.images,
                                qBorrow.price,
                                qItems.title,
                                qItems.itemStatus,
                                qLike.countDistinct().as("likeCount"),
                                qChatChannel.countDistinct().as("chatCount"),
                                qItems.viewCount,
                                qItems.createdAt,
                                qItems.updatedAt
                        )
                )
                .from(qItems)
                .leftJoin(qBorrow).on(qItems.id.eq(qBorrow.id))
                .leftJoin(qLike).on(qItems.id.eq(qLike.id.itemId).and(qLike.delYn.isFalse()))
                .leftJoin(qChatChannel).on(qItems.id.eq(qChatChannel.item.id).and(qChatChannel.delYn.isFalse()))
                .where(qLike.id.userId.eq(userId)
                        .and(qItems.delYn.isFalse()))
                .groupBy(qItems.id)  // 그룹핑 필요
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return qb.fetch();
    }

    @Override
    public void deleteBorrowHistory(String userId, Long borrowSeq) {
        QBorrowHistJpaEntity qBorrowHist = QBorrowHistJpaEntity.borrowHistJpaEntity;
        QUserJpaEntity qUser = QUserJpaEntity.userJpaEntity;

        UserJpaEntity user = queryFactory.selectFrom(qUser).where(qUser.userId.eq(userId)).fetchOne();
        if(user == null) throw new CustomException(ErrorCode.NotFound, "회원 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

        queryFactory.update(qBorrowHist)
                .set(qBorrowHist.delYn, true)
                .set(qBorrowHist.updatedAt, OffsetDateTime.now())
                .where(qBorrowHist.borrowSeq.eq(borrowSeq))
                .execute();
    }
    @Override
    public List<PostsResponse.FindUsersForReviewsResponse> findUsersForReviews(){
        QBorrowHistJpaEntity qBorrowHistJpa= QBorrowHistJpaEntity.borrowHistJpaEntity;
        QItemsReviewJpaEntity qItemsReview=QItemsReviewJpaEntity.itemsReviewJpaEntity;
        QAlarmListJpaEntity qAlarm=QAlarmListJpaEntity.alarmListJpaEntity;
        QAlarmLogJpaEntity qAlarmLog=QAlarmLogJpaEntity.alarmLogJpaEntity;
        JPAQuery<PostsResponse.FindUsersForReviewsResponse> result=queryFactory.select(
                Projections.constructor(
                        PostsResponse.FindUsersForReviewsResponse.class,
                        qBorrowHistJpa.item,
                        qBorrowHistJpa.borrower)
                )
                .from(qBorrowHistJpa)
                .where(
                        Expressions.numberTemplate(Long.class,"DATEDIFF(CURRENT_DATE, {0})",qBorrowHistJpa.endedAt).gt(1),
                        JPAExpressions.selectOne()
                                .from(qItemsReview)
                                .where(qItemsReview.items.eq(qBorrowHistJpa.item),qItemsReview.user.eq(qBorrowHistJpa.borrower))
                                .notExists(),
                        JPAExpressions.selectOne()
                                .from(qAlarm)
                                .join(qAlarmLog)
                                .on(qAlarm.alarmSeq.eq(qAlarmLog.alarmSeq))
                                .where(
                                        qAlarm.pushType.eq(PushType.REVIEW_ALERT), // push_type이 REVIEW
                                        qAlarm.moveToId.eq(qBorrowHistJpa.item.id), // move_to_id와 item_id가 동일
                                        qAlarmLog.userId.eq(qBorrowHistJpa.borrower.userId) // 같은 user_id가 alarm_log에 존재
                                )
                                .notExists()
                );


       // 결과 반환
        List<PostsResponse.FindUsersForReviewsResponse> results = result.fetch();
        return results != null ? results : Collections.emptyList();
    }
    @Override
    public Boolean CheckUsersForReviews(BorrowHistJpaEntity borrowHist){
        QBorrowHistJpaEntity qBorrowHistJpa= QBorrowHistJpaEntity.borrowHistJpaEntity;
        QItemsReviewJpaEntity qItemsReview=QItemsReviewJpaEntity.itemsReviewJpaEntity;

        Boolean exits=queryFactory.selectOne()
                .from(qBorrowHistJpa)
                .leftJoin(qItemsReview).on(
                        qItemsReview.items.eq(borrowHist.getItem())
                                .and(qItemsReview.user.eq(borrowHist.getBorrower()))
                )
                .where(
                        qBorrowHistJpa.item.eq(borrowHist.getItem())
                                .and(qBorrowHistJpa.borrower.eq(borrowHist.getBorrower())
                                .and(qBorrowHistJpa.useYn.isTrue()))
                                .and(qBorrowHistJpa.delYn.isFalse())
                                .and(qItemsReview.isNotNull())
                )
                .fetchFirst()!=null;
        return exits;
    }
}
