package com.sainti.mestemplate.user.adapter.out.persistence.repository;

import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.dto.UserResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Page<UserResult> searchUsers(Long tenantId, UserQuery query, Pageable pageable);
}
