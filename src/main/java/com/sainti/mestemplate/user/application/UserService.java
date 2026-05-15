package com.sainti.mestemplate.user.application;

import com.sainti.mestemplate.global.error.BusinessException;
import com.sainti.mestemplate.user.application.dto.ChangeMyPasswordCommand;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.DeleteUserCommand;
import com.sainti.mestemplate.user.application.dto.ResetPasswordCommand;
import com.sainti.mestemplate.user.application.dto.ResetPasswordResult;
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

import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService implements UserUseCase {

    private static final String TEMP_CHARS =
            "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResult createUser(CreateUserCommand command) {
        if (userRepositoryPort.existsByTenantIdAndLoginId(command.tenantId(), command.loginId())) {
            throw new BusinessException(UserErrorCode.LOGIN_ID_DUPLICATED);
        }

        validatePasswordStrength(command.rawPassword(), command.loginId());

        String passwordHash = passwordEncoder.encode(command.rawPassword());
        User user = User.create(
                command.tenantId(),
                command.loginId(),
                passwordHash,
                command.displayName(),
                command.role()
        );

        return toResult(userRepositoryPort.save(user));
    }

    @Override
    public UserResult updateUser(UpdateUserCommand command) {
        User user = userRepositoryPort.findByTenantIdAndId(command.tenantId(), command.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.update(command.displayName(), command.role(), command.status());

        return toResult(userRepositoryPort.save(user));
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
    public void deleteUser(DeleteUserCommand command) {
        User user = userRepositoryPort.findByTenantIdAndId(command.tenantId(), command.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.delete();
        userRepositoryPort.softDelete(command.tenantId(), command.userId(), command.deletedBy());
    }

    @Override
    public ResetPasswordResult resetPassword(ResetPasswordCommand command) {
        User user = userRepositoryPort.findByTenantIdAndId(command.tenantId(), command.targetUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String temporaryPassword = generateTemporaryPassword();
        user.resetPassword(passwordEncoder.encode(temporaryPassword));
        userRepositoryPort.save(user);

        return new ResetPasswordResult(temporaryPassword);
    }

    @Override
    public void changeMyPassword(ChangeMyPasswordCommand command) {
        User user = userRepositoryPort.findByTenantIdAndId(command.tenantId(), command.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException(UserErrorCode.INVALID_CURRENT_PASSWORD);
        }

        validatePasswordStrength(command.newPassword(), user.getLoginId());

        user.changePassword(passwordEncoder.encode(command.newPassword()));
        userRepositoryPort.save(user);
    }

    private void validatePasswordStrength(String password, String loginId) {
        if (loginId != null && password.toLowerCase().contains(loginId.toLowerCase())) {
            throw new BusinessException(UserErrorCode.WEAK_PASSWORD);
        }
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        char[] password = new char[TEMP_PASSWORD_LENGTH];
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            password[i] = TEMP_CHARS.charAt(random.nextInt(TEMP_CHARS.length()));
        }
        return new String(password);
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
                user.isMustChangePassword(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
