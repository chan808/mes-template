package com.sainti.mestemplate.user.adapter.out.persistence.mapper;

import com.sainti.mestemplate.user.adapter.out.persistence.entity.UserEntity;
import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User user) {
        return UserEntity.of(
                user.getId(),
                user.getTenantId(),
                user.getLoginId(),
                user.getPasswordHash(),
                user.getDisplayName(),
                user.getRole(),
                user.getStatus(),
                user.isDeleted()
        );
    }

    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getTenantId(),
                entity.getLoginId(),
                entity.getPasswordHash(),
                entity.getDisplayName(),
                entity.getRole(),
                entity.getStatus(),
                entity.isDeleted()
        );
    }

    public UserResult toResult(UserEntity entity) {
        return new UserResult(
                entity.getId(),
                entity.getTenantId(),
                entity.getLoginId(),
                entity.getDisplayName(),
                entity.getRole(),
                entity.getStatus(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
