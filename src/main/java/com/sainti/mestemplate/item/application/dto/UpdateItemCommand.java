package com.sainti.mestemplate.item.application.dto;

import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;

public record UpdateItemCommand(
        Long tenantId,
        Long itemId,
        String itemName,
        ItemType itemType,
        String unit,
        ItemStatus status,
        String description
) {
}