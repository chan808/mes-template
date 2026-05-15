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
    private int failedLoginCount;
    private LocalDateTime lastFailedLoginAt;
    private boolean mustChangePassword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User() {
    }

    // 관리자가 생성하는 계정은 mustChangePassword = true (최초 로그인 시 변경 필수)
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
        user.failedLoginCount = 0;
        user.lastFailedLoginAt = null;
        user.mustChangePassword = true;
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
            int failedLoginCount,
            LocalDateTime lastFailedLoginAt,
            boolean mustChangePassword,
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
        user.failedLoginCount = failedLoginCount;
        user.lastFailedLoginAt = lastFailedLoginAt;
        user.mustChangePassword = mustChangePassword;
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

    // 로그인 실패 시 카운트 증가, maxAttempts 초과 시 자동 잠금
    public void recordLoginFailure(int maxAttempts) {
        this.failedLoginCount++;
        this.lastFailedLoginAt = LocalDateTime.now();
        if (this.failedLoginCount >= maxAttempts) {
            this.status = UserStatus.LOCKED;
        }
    }

    public void resetLoginFailure() {
        this.failedLoginCount = 0;
        this.lastFailedLoginAt = null;
    }

    // 관리자가 임시 비밀번호 발급 — LOCKED 상태면 함께 해제
    public void resetPassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.mustChangePassword = true;
        this.failedLoginCount = 0;
        this.lastFailedLoginAt = null;
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
        }
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.mustChangePassword = false;
        this.failedLoginCount = 0;
        this.lastFailedLoginAt = null;
    }

    // 향후 활성 세션·권한 등 삭제 불가 조건 검증 위치
    public void delete() {
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
