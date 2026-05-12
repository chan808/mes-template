package com.sainti.mestemplate.item.application.port.out;

import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ItemRepositoryPort {

    boolean existsByTenantIdAndItemCode(Long tenantId, String itemCode);

    Item save(Item item);

    Optional<Item> findByTenantIdAndId(Long tenantId, Long itemId);

    Page<ItemResult> search(Long tenantId, ItemQuery query, Pageable pageable);

    void softDelete(Long tenantId, Long itemId, Long deletedBy);
}