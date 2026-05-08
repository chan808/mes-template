package com.sainti.mestemplate.item.adapter.in.web.dto;

import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;

public record ItemSearch(
        String itemCode,
        String itemName,
        ItemType itemType,
        ItemStatus status
) {
}
