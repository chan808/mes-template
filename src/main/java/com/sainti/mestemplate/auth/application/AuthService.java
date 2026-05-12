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

    @Override
    public LoginResult login(LoginCommand command) {
        User user = userRepositoryPort.findByTenantIdAndLoginId(command.tenantId(), command.loginId())
                .filter(u -> passwordEncoder.matches(command.rawPassword(), u.getPasswordHash()))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(AuthErrorCode.USER_NOT_ACTIVE);
        }

        String token = jwtProvider.generateToken(user.getId(), user.getTenantId(), user.getRole().name());
        return new LoginResult(token, user.getId(), user.getRole().name());
    }
}
