package com.sainti.mestemplate.auth.application.dto;

public record LoginCommand(Long tenantId, String loginId, String rawPassword) {
}
