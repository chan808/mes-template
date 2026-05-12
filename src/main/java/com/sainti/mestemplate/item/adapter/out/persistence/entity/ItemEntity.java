package com.sainti.mestemplate.item.adapter.out.persistence.entity;

import com.sainti.mestemplate.global.persistence.BaseEntity;
import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_items_tenant_item_code",
                        columnNames = {"tenant_id", "item_code"}
                )
        },
        indexes = {
                @Index(name = "idx_items_tenant_deleted", columnList = "tenant_id, deleted")
        }
)
public class ItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 30)
    private ItemType itemType;

    @Column(nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemStatus status;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean deleted;

    protected ItemEntity() {
    }

    public static ItemEntity of(
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
        ItemEntity entity = new ItemEntity();
        entity.id = id;
        entity.tenantId = tenantId;
        entity.itemCode = itemCode;
        entity.itemName = itemName;
        entity.itemType = itemType;
        entity.unit = unit;
        entity.status = status;
        entity.description = description;
        entity.deleted = deleted;
        return entity;
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

    public void updateFromDomain(String itemName, ItemType itemType, String unit, ItemStatus status, String description, boolean deleted) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.unit = unit;
        this.status = status;
        this.description = description;
        if (!this.deleted && deleted) {
            this.deleted = true;
            softDelete();
        }
    }
}
