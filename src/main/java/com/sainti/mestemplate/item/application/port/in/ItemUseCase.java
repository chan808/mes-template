package com.sainti.mestemplate.item.application.port.in;

import com.sainti.mestemplate.item.application.dto.CreateItemCommand;
import com.sainti.mestemplate.item.application.dto.DeleteItemCommand;
import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.application.dto.UpdateItemCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemUseCase {

    ItemResult createItem(CreateItemCommand command);

    ItemResult updateItem(UpdateItemCommand command);

    ItemResult getItem(Long tenantId, Long itemId);

    Page<ItemResult> searchItems(Long tenantId, ItemQuery query, Pageable pageable);

    void deleteItem(DeleteItemCommand command);
}