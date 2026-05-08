package com.sainti.mestemplate.item.application.dto;

import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;

public record ItemQuery(
        String itemCode,
        String itemName,
        ItemType itemType,
        ItemStatus status
) {
}