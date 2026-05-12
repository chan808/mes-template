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
@Transactional(readOnly = true)
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private static final String DUMMY_PASSWORD_HASH =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6M6jD9xYl8a9kaI0s5momkGLumZ5y";

    @Override
    public LoginResult login(LoginCommand command) {
        User user = userRepositoryPort.findByTenantIdAndLoginId(command.tenantId(), command.loginId())
                .orElse(null);

        String passwordHash = user != null ? user.getPasswordHash() : DUMMY_PASSWORD_HASH;

        boolean validCredentials = passwordEncoder.matches(command.rawPassword(), passwordHash);

        if (user == null || !validCredentials) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(AuthErrorCode.USER_NOT_ACTIVE);
        }

        String token = jwtProvider.generateToken(user.getId(), user.getTenantId(), user.getRole().name());
        return new LoginResult(token, user.getId(), user.getRole().name());
    }
}
