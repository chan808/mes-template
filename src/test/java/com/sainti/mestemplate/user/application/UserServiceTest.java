package com.sainti.mestemplate.user.application;

import com.sainti.mestemplate.global.error.BusinessException;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.application.port.out.UserRepositoryPort;
import com.sainti.mestemplate.user.domain.User;
import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        CreateUserCommand command = new CreateUserCommand(
                1L,
                "operator01",
                "raw-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        LocalDateTime now = LocalDateTime.now();
        User savedUser = User.reconstitute(
                10L,
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR,
                UserStatus.ACTIVE,
                false,
                now,
                now
        );

        when(userRepositoryPort.existsByTenantIdAndLoginId(1L, "operator01")).thenReturn(false);
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        UserResult result = userService.createUser(command);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.tenantId()).isEqualTo(1L);
        assertThat(result.loginId()).isEqualTo("operator01");
        assertThat(result.role()).isEqualTo(UserRole.MES_OPERATOR);
        assertThat(result.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.deleted()).isFalse();
        assertThat(result.createdAt()).isEqualTo(now);
        assertThat(result.updatedAt()).isEqualTo(now);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-password");
    }

    @Test
    void createUserWithDuplicatedLoginIdFails() {
        CreateUserCommand command = new CreateUserCommand(
                1L,
                "operator01",
                "raw-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        when(userRepositoryPort.existsByTenantIdAndLoginId(1L, "operator01")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.LOGIN_ID_DUPLICATED.getMessage());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getUserNotFoundFails() {
        when(userRepositoryPort.findByTenantIdAndId(1L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void deleteUser() {
        User user = User.reconstitute(
                10L,
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR,
                UserStatus.ACTIVE,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepositoryPort.findByTenantIdAndId(1L, 10L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L, 10L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(captor.capture());

        assertThat(captor.getValue().isDeleted()).isTrue();
    }
}
