package com.sainti.mestemplate.item.domain;

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
            boolean deleted
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

    public void delete() {
        this.deleted = true;
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
}
