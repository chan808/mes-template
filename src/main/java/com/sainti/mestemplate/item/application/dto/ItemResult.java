package com.sainti.mestemplate.item.application.dto;

import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;

import java.time.LocalDateTime;

public record ItemResult(
        Long id,
        Long tenantId,
        String itemCode,
        String itemName,
        ItemType itemType,
        String unit,
        ItemStatus status,
        String description,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}