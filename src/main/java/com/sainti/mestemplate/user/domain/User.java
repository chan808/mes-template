package com.sainti.mestemplate.user.domain;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private Long tenantId;
    private String loginId;
    private String passwordHash;
    private String displayName;
    private UserRole role;
    private UserStatus status;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User() {
    }

    public static User create(
            Long tenantId,
            String loginId,
            String passwordHash,
            String displayName,
            UserRole role
    ) {
        User user = new User();
        user.tenantId = tenantId;
        user.loginId = loginId;
        user.passwordHash = passwordHash;
        user.displayName = displayName;
        user.role = role;
        user.status = UserStatus.ACTIVE;
        user.deleted = false;
        return user;
    }

    public static User reconstitute(
            Long id,
            Long tenantId,
            String loginId,
            String passwordHash,
            String displayName,
            UserRole role,
            UserStatus status,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        User user = new User();
        user.id = id;
        user.tenantId = tenantId;
        user.loginId = loginId;
        user.passwordHash = passwordHash;
        user.displayName = displayName;
        user.role = role;
        user.status = status;
        user.deleted = deleted;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }

    public void update(
            String displayName,
            UserRole role,
            UserStatus status
    ) {
        if (role == null) {
            throw new IllegalArgumentException("User role is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("User status is required");
        }

        this.displayName = displayName;
        this.role = role;
        this.status = status;
    }

    public void delete() {
        this.deleted = true;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
