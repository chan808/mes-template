package com.sainti.mestemplate.item.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainti.mestemplate.global.security.JwtAuthenticationFilter;
import com.sainti.mestemplate.global.security.JwtProvider;
import com.sainti.mestemplate.global.security.MesPrincipal;
import com.sainti.mestemplate.global.security.SecurityConfig;
import com.sainti.mestemplate.item.adapter.in.web.dto.ItemCreateRequest;
import com.sainti.mestemplate.item.application.dto.ItemResult;
import com.sainti.mestemplate.item.application.port.in.ItemUseCase;
import com.sainti.mestemplate.item.domain.ItemStatus;
import com.sainti.mestemplate.item.domain.ItemType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ItemController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
class
ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    private static UsernamePasswordAuthenticationToken mockAuth() {
        MesPrincipal principal = new MesPrincipal(100L, 1L);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_MES_OPERATOR"))
        );
    }

    @Test
    void createItem() throws Exception {
        ItemResult result = new ItemResult(
                10L,
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                ItemStatus.ACTIVE,
                "ABS raw material",
                false,
                LocalDateTime.of(2026, 5, 8, 10, 0),
                LocalDateTime.of(2026, 5, 8, 10, 0)
        );

        when(itemUseCase.createItem(any())).thenReturn(result);

        ItemCreateRequest request = new ItemCreateRequest(
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                "ABS raw material"
        );

        mockMvc.perform(post("/api/v1/items")
                        .with(authentication(mockAuth()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.itemCode").value("ITEM-001"))
                .andExpect(jsonPath("$.data.itemName").value("Plastic Resin"))
                .andExpect(jsonPath("$.data.itemType").value("MATERIAL"))
                .andExpect(jsonPath("$.data.unit").value("KG"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        ArgumentCaptor<com.sainti.mestemplate.item.application.dto.CreateItemCommand> captor =
                ArgumentCaptor.forClass(com.sainti.mestemplate.item.application.dto.CreateItemCommand.class);
        verify(itemUseCase).createItem(captor.capture());

        assertThat(captor.getValue().tenantId()).isEqualTo(1L);
        assertThat(captor.getValue().itemCode()).isEqualTo("ITEM-001");
    }

    @Test
    void createItemWithInvalidRequestFails() throws Exception {
        String request = """
                {
                  "itemCode": "",
                  "itemName": "Plastic Resin",
                  "itemType": "MATERIAL",
                  "unit": "KG"
                }
                """;

        mockMvc.perform(post("/api/v1/items")
                        .with(authentication(mockAuth()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("COMMON_001"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("itemCode")));
    }

    @Test
    void getItem() throws Exception {
        ItemResult result = new ItemResult(
                10L,
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                ItemStatus.ACTIVE,
                "ABS raw material",
                false,
                null,
                null
        );

        when(itemUseCase.getItem(1L, 10L)).thenReturn(result);

        mockMvc.perform(get("/api/v1/items/{itemId}", 10L)
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.itemCode").value("ITEM-001"));
    }

    @Test
    void searchItems() throws Exception {
        ItemResult result = new ItemResult(
                10L,
                1L,
                "ITEM-001",
                "Plastic Resin",
                ItemType.MATERIAL,
                "KG",
                ItemStatus.ACTIVE,
                "ABS raw material",
                false,
                null,
                null
        );

        when(itemUseCase.searchItems(eq(1L), any(), any()))
                .thenReturn(new PageImpl<>(List.of(result)));

        mockMvc.perform(get("/api/v1/items")
                        .with(authentication(mockAuth()))
                        .param("itemName", "Resin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(10L))
                .andExpect(jsonPath("$.data.content[0].itemName").value("Plastic Resin"));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/api/v1/items/{itemId}", 10L)
                        .with(authentication(mockAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(itemUseCase).deleteItem(1L, 10L);
    }
}
