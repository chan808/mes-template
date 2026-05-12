package com.sainti.mestemplate.user.application.port.out;

import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryPort {

    boolean existsByTenantIdAndLoginId(Long tenantId, String loginId);

    User save(User user);

    Optional<User> findByTenantIdAndId(Long tenantId, Long userId);

    Optional<User> findByTenantIdAndLoginId(Long tenantId, String loginId);

    Page<UserResult> search(Long tenantId, UserQuery query, Pageable pageable);
}
