package com.sainti.mestemplate.item.domain;

import java.time.LocalDateTime;

public class Item {

    private Long id;
    private Long tenantId;
    private String itemCode;
    private String itemName;
    private ItemType itemType;
    private String unit;
    private ItemStatus status;
    private String description;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Item() {
    }

    public static Item create(
            Long tenantId,
            String itemCode,
            String itemName,
            ItemType itemType,
            String unit,
            String description
    ) {
        Item item = new Item();
        item.tenantId = tenantId;
        item.itemCode = itemCode;
        item.itemName = itemName;
        item.itemType = itemType;
        item.unit = unit;
        item.status = ItemStatus.ACTIVE;
        item.description = description;
        item.deleted = false;
        return item;
    }

    public static Item reconstitute(
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
        Item item = new Item();
        item.id = id;
        item.tenantId = tenantId;
        item.itemCode = itemCode;
        item.itemName = itemName;
        item.itemType = itemType;
        item.unit = unit;
        item.status = status;
        item.description = description;
        item.deleted = deleted;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        return item;
    }

    public void update(
            String itemName,
            ItemType itemType,
            String unit,
            ItemStatus status,
            String description
    ) {
        if (status == null) {
            throw new IllegalArgumentException("Item status is required");
        }

        this.itemName = itemName;
        this.itemType = itemType;
        this.unit = unit;
        this.status = status;
        this.description = description;
    }

    // 향후 재고 잔량·진행 중인 작업지시 등 삭제 불가 조건 검증 위치
    public void delete() {
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getUnit() {
        return unit;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
