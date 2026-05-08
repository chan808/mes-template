package com.sainti.mestemplate.user.adapter.out.persistence.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sainti.mestemplate.user.adapter.out.persistence.entity.QUserEntity;
import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.dto.UserResult;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public UserQueryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<UserResult> searchUsers(Long tenantId, UserQuery query, Pageable pageable) {
        QUserEntity user = QUserEntity.userEntity;
        BooleanBuilder condition = buildCondition(user, tenantId, query);

        List<UserResult> content = queryFactory
                .select(Projections.constructor(
                        UserResult.class,
                        user.id,
                        user.tenantId,
                        user.loginId,
                        user.displayName,
                        user.role,
                        user.status,
                        user.deleted,
                        user.createdAt,
                        user.updatedAt
                ))
                .from(user)
                .where(condition)
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanBuilder buildCondition(QUserEntity user, Long tenantId, UserQuery query) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(user.tenantId.eq(tenantId));
        condition.and(user.deleted.isFalse());

        if (query == null) {
            return condition;
        }

        if (StringUtils.hasText(query.loginId())) {
            condition.and(user.loginId.containsIgnoreCase(query.loginId()));
        }
        if (StringUtils.hasText(query.displayName())) {
            condition.and(user.displayName.containsIgnoreCase(query.displayName()));
        }
        if (query.role() != null) {
            condition.and(user.role.eq(query.role()));
        }
        if (query.status() != null) {
            condition.and(user.status.eq(query.status()));
        }

        return condition;
    }
}
