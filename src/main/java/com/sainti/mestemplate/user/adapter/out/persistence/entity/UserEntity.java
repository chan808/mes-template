package com.sainti.mestemplate.user.adapter.out.persistence.entity;

import com.sainti.mestemplate.global.persistence.BaseEntity;
import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                // deleted 포함으로 soft delete 충돌 방지 — Flyway 도입 시 WHERE deleted = false partial index로 교체
                @UniqueConstraint(
                        name = "uk_users_tenant_login_id_deleted",
                        columnNames = {"tenant_id", "login_id", "deleted"}
                )
        },
        indexes = {
                @Index(name = "idx_users_tenant_deleted", columnList = "tenant_id, deleted")
        }
)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status;

    @Column(nullable = false)
    private boolean deleted;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    protected UserEntity() {
    }

    public static UserEntity of(
            Long id,
            Long tenantId,
            String loginId,
            String passwordHash,
            String displayName,
            UserRole role,
            UserStatus status,
            boolean deleted,
            int failedLoginCount,
            LocalDateTime lastFailedLoginAt,
            boolean mustChangePassword
    ) {
        UserEntity entity = new UserEntity();
        entity.id = id;
        entity.tenantId = tenantId;
        entity.loginId = loginId;
        entity.passwordHash = passwordHash;
        entity.displayName = displayName;
        entity.role = role;
        entity.status = status;
        entity.deleted = deleted;
        entity.failedLoginCount = failedLoginCount;
        entity.lastFailedLoginAt = lastFailedLoginAt;
        entity.mustChangePassword = mustChangePassword;
        return entity;
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getLoginId() { return loginId; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public boolean isDeleted() { return deleted; }
    public int getFailedLoginCount() { return failedLoginCount; }
    public LocalDateTime getLastFailedLoginAt() { return lastFailedLoginAt; }
    public boolean isMustChangePassword() { return mustChangePassword; }

    // 모든 가변 필드를 도메인 값으로 동기화
    public void updateFromDomain(
            String displayName, UserRole role, UserStatus status,
            String passwordHash, int failedLoginCount, LocalDateTime lastFailedLoginAt,
            boolean mustChangePassword
    ) {
        this.displayName = displayName;
        this.role = role;
        this.status = status;
        this.passwordHash = passwordHash;
        this.failedLoginCount = failedLoginCount;
        this.lastFailedLoginAt = lastFailedLoginAt;
        this.mustChangePassword = mustChangePassword;
    }

    public void softDelete(Long deletedBy) {
        this.deleted = true;
        super.softDelete(deletedBy);
    }
}
