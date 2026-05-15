package com.sainti.mestemplate.user.application.port.in;

import com.sainti.mestemplate.user.application.dto.ChangeMyPasswordCommand;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.DeleteUserCommand;
import com.sainti.mestemplate.user.application.dto.ResetPasswordCommand;
import com.sainti.mestemplate.user.application.dto.ResetPasswordResult;
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

    void deleteUser(DeleteUserCommand command);

    // TENANT_ADMIN이 대상 사용자의 임시 비밀번호를 발급하고 mustChangePassword = true 설정
    ResetPasswordResult resetPassword(ResetPasswordCommand command);

    // 사용자가 자신의 비밀번호를 변경 — mustChangePassword = false 해제
    void changeMyPassword(ChangeMyPasswordCommand command);
}
