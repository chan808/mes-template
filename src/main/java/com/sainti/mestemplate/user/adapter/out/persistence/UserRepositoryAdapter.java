package com.sainti.mestemplate.user.adapter.out.persistence;

import com.sainti.mestemplate.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.sainti.mestemplate.user.adapter.out.persistence.repository.UserJpaRepository;
import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.application.port.out.UserRepositoryPort;
import com.sainti.mestemplate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public boolean existsByTenantIdAndLoginId(Long tenantId, String loginId) {
        return userJpaRepository.existsByTenantIdAndLoginIdAndDeletedFalse(tenantId, loginId);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return userPersistenceMapper.toDomain(
                    userJpaRepository.save(userPersistenceMapper.toEntity(user))
            );
        }
        // id가 있으면 이미 서비스 트랜잭션에서 로드된 managed entity가 1차 캐시에 존재한다.
        // 새 객체를 만들지 않고 필드만 수정 → JPA dirty-checking이 UPDATE를 생성,
        // saveAndFlush로 @PreUpdate를 즉시 발동시켜 updatedAt을 반영한다.
        UserEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("UserEntity not found: " + user.getId()));
        userPersistenceMapper.updateEntity(entity, user);
        return userPersistenceMapper.toDomain(userJpaRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<User> findByTenantIdAndId(Long tenantId, Long userId) {
        return userJpaRepository.findByTenantIdAndIdAndDeletedFalse(tenantId, userId)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByTenantIdAndLoginId(Long tenantId, String loginId) {
        return userJpaRepository.findByTenantIdAndLoginIdAndDeletedFalse(tenantId, loginId)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Page<UserResult> search(Long tenantId, UserQuery query, Pageable pageable) {
        return userJpaRepository.searchUsers(tenantId, query, pageable);
    }
}
