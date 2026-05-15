package com.sainti.mestemplate.auth.application;

import com.sainti.mestemplate.auth.application.dto.LoginCommand;
import com.sainti.mestemplate.auth.application.dto.LoginResult;
import com.sainti.mestemplate.auth.application.port.in.AuthUseCase;
import com.sainti.mestemplate.global.error.BusinessException;
import com.sainti.mestemplate.global.security.JwtProvider;
import com.sainti.mestemplate.user.application.port.out.UserRepositoryPort;
import com.sainti.mestemplate.user.domain.User;
import com.sainti.mestemplate.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService implements AuthUseCase {

    private static final String DUMMY_PASSWORD_HASH =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6M6jD9xYl8a9kaI0s5momkGLumZ5y";
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public LoginResult login(LoginCommand command) {
        User user = userRepositoryPort.findByTenantIdAndLoginId(command.tenantId(), command.loginId())
                .orElse(null);

        // 사용자 미존재 여부와 무관하게 동일 시간 소요 — 타이밍 공격 방지
        String passwordHash = user != null ? user.getPasswordHash() : DUMMY_PASSWORD_HASH;
        boolean validCredentials = passwordEncoder.matches(command.rawPassword(), passwordHash);

        if (user == null || !validCredentials) {
            if (user != null) {
                user.recordLoginFailure(MAX_LOGIN_ATTEMPTS);
                userRepositoryPort.save(user);
            }
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(AuthErrorCode.USER_NOT_ACTIVE);
        }

        user.resetLoginFailure();
        userRepositoryPort.save(user);

        String token = jwtProvider.generateToken(user.getId(), user.getTenantId());
        return new LoginResult(token, user.getId(), user.getRole().name(), user.isMustChangePassword());
    }
}
