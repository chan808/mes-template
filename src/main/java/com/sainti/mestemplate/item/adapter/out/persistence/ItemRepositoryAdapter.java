package com.sainti.mestemplate.item.adapter.out.persistence;

import com.sainti.mestemplate.item.adapter.out.persistence.entity.ItemEntity;
import com.sainti.mestemplate.item.adapter.out.persistence.mapper.ItemPersistenceMapper;
import com.sainti.mestemplate.item.adapter.out.persistence.repository.ItemJpaRepository;
import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.application.port.out.ItemRepositoryPort;
import com.sainti.mestemplate.item.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ItemRepositoryAdapter implements ItemRepositoryPort {

    private final ItemJpaRepository itemJpaRepository;
    private final ItemPersistenceMapper itemPersistenceMapper;

    @Override
    public boolean existsByTenantIdAndItemCode(Long tenantId, String itemCode) {
        return itemJpaRepository.existsByTenantIdAndItemCodeAndDeletedFalse(tenantId, itemCode);
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            return itemPersistenceMapper.toDomain(
                    itemJpaRepository.save(itemPersistenceMapper.toEntity(item))
            );
        }
        ItemEntity entity = itemJpaRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalStateException("ItemEntity not found: " + item.getId()));
        itemPersistenceMapper.updateEntity(entity, item);
        return itemPersistenceMapper.toDomain(itemJpaRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<Item> findByTenantIdAndId(Long tenantId, Long itemId) {
        return itemJpaRepository.findByTenantIdAndIdAndDeletedFalse(tenantId, itemId)
                .map(itemPersistenceMapper::toDomain);
    }

    @Override
    public Page<ItemResult> search(Long tenantId, ItemQuery query, Pageable pageable) {
        return itemJpaRepository.searchItems(tenantId, query, pageable);
    }
}
