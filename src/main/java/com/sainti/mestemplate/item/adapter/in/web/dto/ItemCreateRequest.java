package com.sainti.mestemplate.item.adapter.in.web.dto;

import com.sainti.mestemplate.item.domain.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemCreateRequest(
        @NotBlank
        @Size(max = 50)
        String itemCode,

        @NotBlank
        @Size(max = 100)
        String itemName,

        @NotNull
        ItemType itemType,

        @NotBlank
        @Size(max = 20)
        String unit,

        @Size(max = 500)
        String description
) {
}
