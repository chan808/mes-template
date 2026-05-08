package com.sainti.mestemplate.item.application;

import com.sainti.mestemplate.global.error.BusinessException;
import com.sainti.mestemplate.item.application.dto.CreateItemCommand;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.application.port.out.ItemRepositoryPort;
import com.sainti.mestemplate.item.domain.Item;
import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepositoryPort itemRepositoryPort;

    @InjectMocks
    private ItemService itemService;

    @Test
    void createItem() {
        CreateItemCommand command = new CreateItemCommand(
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        Item savedItem = Item.reconstitute(
                10L,
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                ItemStatus.ACTIVE,
                "ABS raw material",
                false
        );

        when(itemRepositoryPort.existsByTenantIdAndItemCode(1L, "ITEM-001")).thenReturn(false);
        when(itemRepositoryPort.save(any(Item.class))).thenReturn(savedItem);

        ItemResult result = itemService.createItem(command);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.tenantId()).isEqualTo(1L);
        assertThat(result.itemCode()).isEqualTo("ITEM-001");
        assertThat(result.status()).isEqualTo(ItemStatus.ACTIVE);
        assertThat(result.deleted()).isFalse();
    }

    @Test
    void createItemWithDuplicatedCodeFails() {
        CreateItemCommand command = new CreateItemCommand(
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        when(itemRepositoryPort.existsByTenantIdAndItemCode(1L, "ITEM-001")).thenReturn(true);

        assertThatThrownBy(() -> itemService.createItem(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ItemErrorCode.ITEM_CODE_DUPLICATED.getMessage());
    }

    @Test
    void getItemNotFoundFails() {
        when(itemRepositoryPort.findByTenantIdAndId(1L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ItemErrorCode.ITEM_NOT_FOUND.getMessage());
    }

    @Test
    void deleteItem() {
        Item item = Item.reconstitute(
                10L,
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                ItemStatus.ACTIVE,
                "ABS raw material",
                false
        );

        when(itemRepositoryPort.findByTenantIdAndId(1L, 10L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L, 10L);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepositoryPort).save(captor.capture());

        assertThat(captor.getValue().isDeleted()).isTrue();
    }
}
