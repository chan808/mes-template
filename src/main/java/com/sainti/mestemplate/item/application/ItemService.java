package com.sainti.mestemplate.item.application;

import com.sainti.mestemplate.global.error.BusinessException;
import com.sainti.mestemplate.item.application.dto.CreateItemCommand;
import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.application.dto.UpdateItemCommand;
import com.sainti.mestemplate.item.application.port.in.ItemUseCase;
import com.sainti.mestemplate.item.application.port.out.ItemRepositoryPort;
import com.sainti.mestemplate.item.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemService implements ItemUseCase {

    private final ItemRepositoryPort itemRepositoryPort;

    @Override
    public ItemResult createItem(CreateItemCommand command) {
        if (itemRepositoryPort.existsByTenantIdAndItemCode(command.tenantId(), command.itemCode())) {
            throw new BusinessException(ItemErrorCode.ITEM_CODE_DUPLICATED);
        }

        Item item = Item.create(
                command.tenantId(),
                command.itemCode(),
                command.itemName(),
                command.itemType(),
                command.unit(),
                command.description()
        );

        Item savedItem = itemRepositoryPort.save(item);

        return toResult(savedItem);
    }

    @Override
    public ItemResult updateItem(UpdateItemCommand command) {
        Item item = itemRepositoryPort.findByTenantIdAndId(command.tenantId(), command.itemId())
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));

        item.update(
                command.itemName(),
                command.itemType(),
                command.unit(),
                command.status(),
                command.description()
        );

        Item savedItem = itemRepositoryPort.save(item);

        return toResult(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResult getItem(Long tenantId, Long itemId) {
        Item item = itemRepositoryPort.findByTenantIdAndId(tenantId, itemId)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));

        return toResult(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemResult> searchItems(Long tenantId, ItemQuery query, Pageable pageable) {
        return itemRepositoryPort.search(tenantId, query, pageable);
    }

    @Override
    public void deleteItem(Long tenantId, Long itemId) {
        Item item = itemRepositoryPort.findByTenantIdAndId(tenantId, itemId)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));

        item.delete();

        itemRepositoryPort.save(item);
    }

    private ItemResult toResult(Item item) {
        return new ItemResult(
                item.getId(),
                item.getTenantId(),
                item.getItemCode(),
                item.getItemName(),
                item.getItemType(),
                item.getUnit(),
                item.getStatus(),
                item.getDescription(),
                item.isDeleted(),
                null,
                null
        );
    }
}
