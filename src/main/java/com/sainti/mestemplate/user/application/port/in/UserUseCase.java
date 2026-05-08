package com.sainti.mestemplate.user.application.port.in;

import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.UpdateUserCommand;
import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.dto.UserResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserUseCase {

    UserResult createUser(CreateUserCommand command);

    UserResult updateUser(UpdateUserCommand command);

    UserResult getUser(Long tenantId, Long userId);

    Page<UserResult> searchUsers(Long tenantId, UserQuery query, Pageable pageable);

    void deleteUser(Long tenantId, Long userId);
}
