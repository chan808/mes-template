package com.sainti.mestemplate.item.application.dto;

public record DeleteItemCommand(Long tenantId, Long itemId, Long deletedBy) {}
