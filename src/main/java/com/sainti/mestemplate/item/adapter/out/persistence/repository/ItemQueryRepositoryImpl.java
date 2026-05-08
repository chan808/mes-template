package com.sainti.mestemplate.item.adapter.out.persistence.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sainti.mestemplate.item.adapter.out.persistence.entity.QItemEntity;
import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ItemQueryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<ItemResult> searchItems(Long tenantId, ItemQuery query, Pageable pageable) {
        QItemEntity item = QItemEntity.itemEntity;
        BooleanBuilder condition = buildCondition(item, tenantId, query);

        List<ItemResult> content = queryFactory
                .select(Projections.constructor(
                        ItemResult.class,
                        item.id,
                        item.tenantId,
                        item.itemCode,
                        item.itemName,
                        item.itemType,
                        item.unit,
                        item.status,
                        item.description,
                        item.deleted,
                        item.createdAt,
                        item.updatedAt
                ))
                .from(item)
                .where(condition)
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(item.count())
                .from(item)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanBuilder buildCondition(QItemEntity item, Long tenantId, ItemQuery query) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(item.tenantId.eq(tenantId));
        condition.and(item.deleted.isFalse());

        if (query == null) {
            return condition;
        }

        if (StringUtils.hasText(query.itemCode())) {
            condition.and(item.itemCode.containsIgnoreCase(query.itemCode()));
        }
        if (StringUtils.hasText(query.itemName())) {
            condition.and(item.itemName.containsIgnoreCase(query.itemName()));
        }
        if (query.itemType() != null) {
            condition.and(item.itemType.eq(query.itemType()));
        }
        if (query.status() != null) {
            condition.and(item.status.eq(query.status()));
        }

        return condition;
    }
}
