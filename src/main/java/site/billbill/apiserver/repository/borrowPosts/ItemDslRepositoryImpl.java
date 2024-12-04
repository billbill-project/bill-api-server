package site.billbill.apiserver.repository.borrowPosts;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import site.billbill.apiserver.model.post.ItemsJpaEntity;
import site.billbill.apiserver.model.post.QItemsBorrowJpaEntity;
import site.billbill.apiserver.model.post.QItemsCategoryJpaEntity;
import site.billbill.apiserver.model.post.QItemsJpaEntity;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
public class ItemDslRepositoryImpl implements ItemDslRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemsJpaEntity> findItemsWithConditions(String category, Pageable pageable, String sortField,String keyword) {
        QItemsJpaEntity items = QItemsJpaEntity.itemsJpaEntity;
        QItemsBorrowJpaEntity borrow = QItemsBorrowJpaEntity.itemsBorrowJpaEntity;
        QItemsCategoryJpaEntity categoryEntity = QItemsCategoryJpaEntity.itemsCategoryJpaEntity;



        JPAQuery<ItemsJpaEntity> query = queryFactory.selectFrom(items)
                .leftJoin(borrow).on(items.id.eq(borrow.item.id)) // 식별 관계 조인
                .where(items.delYn.isFalse());

        // 카테고리 필터링
        if (!"entire".equals(category)) {
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
        if(keyword !=null && !keyword.isEmpty()){
            query.where(applyKeywordFilter(items,keyword));
        }

        // 정렬 조건 처리
    pageable.getSort().forEach(order -> {
        OrderSpecifier<?> orderSpecifier;
        switch (order.getProperty()) {
            case "price" -> orderSpecifier = order.isAscending() ? borrow.price.asc() : borrow.price.desc();
            case "createdAt" -> orderSpecifier = order.isAscending() ? items.createdAt.asc() : items.createdAt.desc();
            case "likeCount" -> orderSpecifier = order.isAscending() ? items.likeCount.asc() : items.likeCount.desc();
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


}
