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
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
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
}
