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
        return userPersistenceMapper.toDomain(
                userJpaRepository.save(userPersistenceMapper.toEntity(user))
        );
    }

    @Override
    public Optional<User> findByTenantIdAndId(Long tenantId, Long userId) {
        return userJpaRepository.findByTenantIdAndIdAndDeletedFalse(tenantId, userId)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Page<UserResult> search(Long tenantId, UserQuery query, Pageable pageable) {
        return userJpaRepository.searchUsers(tenantId, query, pageable);
    }
}
