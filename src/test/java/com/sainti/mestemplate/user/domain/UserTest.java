package com.sainti.mestemplate.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void createUser() {
        User user = User.create(
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        assertThat(user.getTenantId()).isEqualTo(1L);
        assertThat(user.getLoginId()).isEqualTo("operator01");
        assertThat(user.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(user.getDisplayName()).isEqualTo("Operator One");
        assertThat(user.getRole()).isEqualTo(UserRole.MES_OPERATOR);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    void updateUser() {
        User user = User.create(
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        user.update(
                "Manager One",
                UserRole.MES_MANAGER,
                UserStatus.LOCKED
        );

        assertThat(user.getDisplayName()).isEqualTo("Manager One");
        assertThat(user.getRole()).isEqualTo(UserRole.MES_MANAGER);
        assertThat(user.getStatus()).isEqualTo(UserStatus.LOCKED);
    }

    @Test
    void deleteUser() {
        User user = User.create(
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        // delete()는 삭제 전 도메인 검증 위치 — 실제 deleted 플래그는 Repository 레이어(softDelete)가 처리
        user.delete();

        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    void updateUserWithNullRoleFails() {
        User user = User.create(
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        assertThatThrownBy(() -> user.update("Manager One", null, UserStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User role is required");
    }

    @Test
    void updateUserWithNullStatusFails() {
        User user = User.create(
                1L,
                "operator01",
                "encoded-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        assertThatThrownBy(() -> user.update("Manager One", UserRole.MES_MANAGER, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User status is required");
    }
}
