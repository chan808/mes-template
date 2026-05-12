package com.sainti.mestemplate.item.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ItemTest {

    @Test
    void createItem() {
        Item item = Item.create(
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        assertThat(item.getTenantId()).isEqualTo(1L);
        assertThat(item.getItemCode()).isEqualTo("ITEM-001");
        assertThat(item.getItemName()).isEqualTo("Plastic Resin");
        assertThat(item.getItemType()).isEqualTo(ItemType.MATERIAL);
        assertThat(item.getUnit()).isEqualTo("KG");
        assertThat(item.getStatus()).isEqualTo(ItemStatus.ACTIVE);
        assertThat(item.getDescription()).isEqualTo("ABS raw material");
        assertThat(item.isDeleted()).isFalse();
    }

    @Test
    void updateItem() {
        Item item = Item.create(
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        item.update(
                "Plastic Resin Updated",
                ItemType.MATERIAL,
                "BAG",
                ItemStatus.INACTIVE,
                "Updated description"
        );

        assertThat(item.getItemName()).isEqualTo("Plastic Resin Updated");
        assertThat(item.getItemType()).isEqualTo(ItemType.MATERIAL);
        assertThat(item.getUnit()).isEqualTo("BAG");
        assertThat(item.getStatus()).isEqualTo(ItemStatus.INACTIVE);
        assertThat(item.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void deleteDoesNotThrowForDeletableItem() {
        Item item = Item.create(
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        assertThatNoException().isThrownBy(item::delete);
    }

    @Test
    void updateItemWithNullStatusFails() {
        Item item = Item.create(
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        assertThatThrownBy(() -> item.update(
                "Plastic Resin Updated",
                ItemType.MATERIAL,
                "BAG",
                null,
                "Updated description"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item status is required");
    }
}
