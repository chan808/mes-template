package com.sainti.mestemplate.item.adapter.in.web.dto;

import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;

import java.time.LocalDateTime;

public record ItemResponse(
        Long id,
        String itemCode,
        String itemName,
        ItemType itemType,
        String unit,
        ItemStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ItemResponse from(ItemResult result) {
        return new ItemResponse(
                result.id(),
                result.itemCode(),
                result.itemName(),
                result.itemType(),
                result.unit(),
                result.status(),
                result.description(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
