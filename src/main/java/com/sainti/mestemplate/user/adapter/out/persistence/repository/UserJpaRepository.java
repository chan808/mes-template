package com.sainti.mestemplate.user.adapter.out.persistence.repository;

import com.sainti.mestemplate.user.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, UserQueryRepository {

    boolean existsByTenantIdAndLoginIdAndDeletedFalse(Long tenantId, String loginId);

    Optional<UserEntity> findByTenantIdAndIdAndDeletedFalse(Long tenantId, Long id);
}
