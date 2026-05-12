package com.sainti.mestemplate.user.application.dto;

public record DeleteUserCommand(Long tenantId, Long userId, Long deletedBy) {}
