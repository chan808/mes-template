package com.sainti.mestemplate.user.application;

import com.sainti.mestemplate.global.error.BusinessException;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.UpdateUserCommand;
import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.application.port.in.UserUseCase;
import com.sainti.mestemplate.user.application.port.out.UserRepositoryPort;
import com.sainti.mestemplate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResult createUser(CreateUserCommand command) {
        if (userRepositoryPort.existsByTenantIdAndLoginId(command.tenantId(), command.loginId())) {
            throw new BusinessException(UserErrorCode.LOGIN_ID_DUPLICATED);
        }

        String passwordHash = passwordEncoder.encode(command.rawPassword());
        User user = User.create(
                command.tenantId(),
                command.loginId(),
                passwordHash,
                command.displayName(),
                command.role()
        );

        User savedUser = userRepositoryPort.save(user);

        return toResult(savedUser);
    }

    @Override
    public UserResult updateUser(UpdateUserCommand command) {
        User user = userRepositoryPort.findByTenantIdAndId(command.tenantId(), command.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.update(
                command.displayName(),
                command.role(),
                command.status()
        );

        User savedUser = userRepositoryPort.save(user);

        return toResult(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult getUser(Long tenantId, Long userId) {
        User user = userRepositoryPort.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return toResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResult> searchUsers(Long tenantId, UserQuery query, Pageable pageable) {
        return userRepositoryPort.search(tenantId, query, pageable);
    }

    @Override
    public void deleteUser(Long tenantId, Long userId) {
        User user = userRepositoryPort.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.delete();

        userRepositoryPort.save(user);
    }

    private UserResult toResult(User user) {
        return new UserResult(
                user.getId(),
                user.getTenantId(),
                user.getLoginId(),
                user.getDisplayName(),
                user.getRole(),
                user.getStatus(),
                user.isDeleted(),
                null,
                null
        );
    }
}
