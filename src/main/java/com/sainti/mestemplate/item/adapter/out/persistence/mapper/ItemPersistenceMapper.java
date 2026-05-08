package com.sainti.mestemplate.item.adapter.out.persistence.mapper;

import com.sainti.mestemplate.item.adapter.out.persistence.entity.ItemEntity;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.domain.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemPersistenceMapper {

    public ItemEntity toEntity(Item item) {
        return ItemEntity.of(
                item.getId(),
                item.getTenantId(),
                item.getItemCode(),
                item.getItemName(),
                item.getItemType(),
                item.getUnit(),
                item.getStatus(),
                item.getDescription(),
                item.isDeleted()
        );
    }

    public Item toDomain(ItemEntity entity) {
        return Item.reconstitute(
                entity.getId(),
                entity.getTenantId(),
                entity.getItemCode(),
                entity.getItemName(),
                entity.getItemType(),
                entity.getUnit(),
                entity.getStatus(),
                entity.getDescription(),
                entity.isDeleted()
        );
    }

    public ItemResult toResult(ItemEntity entity) {
        return new ItemResult(
                entity.getId(),
                entity.getTenantId(),
                entity.getItemCode(),
                entity.getItemName(),
                entity.getItemType(),
                entity.getUnit(),
                entity.getStatus(),
                entity.getDescription(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
