package com.sainti.mestemplate.item.adapter.out.persistence.repository;

import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemQueryRepository {

    Page<ItemResult> searchItems(Long tenantId, ItemQuery query, Pageable pageable);
}
