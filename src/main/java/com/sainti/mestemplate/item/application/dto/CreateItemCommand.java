package com.sainti.mestemplate.item.application.dto;

import com.sainti.mestemplate.item.domain.ItemType;

public record CreateItemCommand (
    Long tenantId,
    String itemCode,
    String itemName,
    ItemType itemType,
    String unit,
    String description
) {
}
