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

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_tenant_login_id",
                        columnNames = {"tenant_id", "login_id"}
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
            boolean deleted
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
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // update 경로에서 managed entity에 도메인 상태를 반영한다. 새 객체를 만들지 않아 createdAt 등 audit 필드가 유지된다.
    public void updateFromDomain(String displayName, UserRole role, UserStatus status, boolean deleted) {
        this.displayName = displayName;
        this.role = role;
        this.status = status;
        if (!this.deleted && deleted) {
            this.deleted = true;
            softDelete();
        }
    }
}
