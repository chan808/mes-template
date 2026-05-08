package com.sainti.mestemplate.item.adapter.out.persistence.repository;

import com.sainti.mestemplate.item.adapter.out.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long>, ItemQueryRepository {

    boolean existsByTenantIdAndItemCodeAndDeletedFalse(Long tenantId, String itemCode);

    Optional<ItemEntity> findByTenantIdAndIdAndDeletedFalse(Long tenantId, Long id);
}
