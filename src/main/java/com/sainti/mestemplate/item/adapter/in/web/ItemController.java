package com.sainti.mestemplate.item.adapter.in.web;

import com.sainti.mestemplate.global.response.ApiResponse;
import com.sainti.mestemplate.global.security.MesPrincipal;
import com.sainti.mestemplate.item.adapter.in.web.dto.ItemCreateRequest;
import com.sainti.mestemplate.item.adapter.in.web.dto.ItemResponse;
import com.sainti.mestemplate.item.adapter.in.web.dto.ItemSearch;
import com.sainti.mestemplate.item.adapter.in.web.dto.ItemUpdateRequest;
import com.sainti.mestemplate.item.application.dto.CreateItemCommand;
import com.sainti.mestemplate.item.application.dto.DeleteItemCommand;
import com.sainti.mestemplate.item.application.dto.ItemQuery;
import com.sainti.mestemplate.item.application.dto.UpdateItemCommand;
import com.sainti.mestemplate.item.application.port.in.ItemUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemUseCase itemUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(
            @AuthenticationPrincipal MesPrincipal principal,
            @Valid @RequestBody ItemCreateRequest request
    ) {
        CreateItemCommand command = new CreateItemCommand(
                principal.tenantId(),
                request.itemCode(),
                request.itemName(),
                request.itemType(),
                request.unit(),
                request.description()
        );

        ItemResponse response = ItemResponse.from(itemUseCase.createItem(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemUpdateRequest request
    ) {
        UpdateItemCommand command = new UpdateItemCommand(
                principal.tenantId(),
                itemId,
                request.itemName(),
                request.itemType(),
                request.unit(),
                request.status(),
                request.description()
        );

        ItemResponse response = ItemResponse.from(itemUseCase.updateItem(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItem(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long itemId
    ) {
        ItemResponse response = ItemResponse.from(
                itemUseCase.getItem(principal.tenantId(), itemId));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ItemResponse>>> searchItems(
            @AuthenticationPrincipal MesPrincipal principal,
            ItemSearch search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ItemQuery query = new ItemQuery(
                search.itemCode(),
                search.itemName(),
                search.itemType(),
                search.status()
        );

        Page<ItemResponse> response = itemUseCase.searchItems(
                        principal.tenantId(), query, pageable)
                .map(ItemResponse::from);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long itemId
    ) {
        DeleteItemCommand command = new DeleteItemCommand(
                principal.tenantId(), itemId, principal.userId()
        );
        itemUseCase.deleteItem(command);

        return ResponseEntity.ok(ApiResponse.successVoid());
    }
}
