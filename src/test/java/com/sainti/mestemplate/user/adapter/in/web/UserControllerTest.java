package com.sainti.mestemplate.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainti.mestemplate.global.security.JwtAuthenticationFilter;
import com.sainti.mestemplate.global.security.JwtProvider;
import com.sainti.mestemplate.global.security.MesPrincipal;
import com.sainti.mestemplate.global.security.SecurityConfig;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserCreateRequest;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.application.port.in.UserUseCase;
import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;
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
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    private static UsernamePasswordAuthenticationToken mockAuth() {
        MesPrincipal principal = new MesPrincipal(100L, 1L);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_TENANT_ADMIN"))
        );
    }

    @Test
    void createUser() throws Exception {
        UserResult result = new UserResult(
                10L,
                1L,
                "operator01",
                "Operator One",
                UserRole.MES_OPERATOR,
                UserStatus.ACTIVE,
                false,
                LocalDateTime.of(2026, 5, 8, 10, 0),
                LocalDateTime.of(2026, 5, 8, 10, 0)
        );

        when(userUseCase.createUser(any())).thenReturn(result);

        UserCreateRequest request = new UserCreateRequest(
                "operator01",
                "raw-password",
                "Operator One",
                UserRole.MES_OPERATOR
        );

        mockMvc.perform(post("/api/v1/users")
                        .with(authentication(mockAuth()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.loginId").value("operator01"))
                .andExpect(jsonPath("$.data.displayName").value("Operator One"))
                .andExpect(jsonPath("$.data.role").value("MES_OPERATOR"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userUseCase).createUser(captor.capture());

        assertThat(captor.getValue().tenantId()).isEqualTo(1L);
        assertThat(captor.getValue().loginId()).isEqualTo("operator01");
        assertThat(captor.getValue().rawPassword()).isEqualTo("raw-password");
    }

    @Test
    void createUserWithInvalidRequestFails() throws Exception {
        String request = """
                {
                  "loginId": "",
                  "password": "short",
                  "displayName": "Operator One",
                  "role": "MES_OPERATOR"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .with(authentication(mockAuth()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("COMMON_001"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("loginId")));
    }

    @Test
    void getUser() throws Exception {
        UserResult result = new UserResult(
                10L,
                1L,
                "operator01",
                "Operator One",
                UserRole.MES_OPERATOR,
                UserStatus.ACTIVE,
                false,
                null,
                null
        );

        when(userUseCase.getUser(1L, 10L)).thenReturn(result);

        mockMvc.perform(get("/api/v1/users/{userId}", 10L)
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.loginId").value("operator01"));
    }

    @Test
    void searchUsers() throws Exception {
        UserResult result = new UserResult(
                10L,
                1L,
                "operator01",
                "Operator One",
                UserRole.MES_OPERATOR,
                UserStatus.ACTIVE,
                false,
                null,
                null
        );

        when(userUseCase.searchUsers(eq(1L), any(), any()))
                .thenReturn(new PageImpl<>(List.of(result)));

        mockMvc.perform(get("/api/v1/users")
                        .with(authentication(mockAuth()))
                        .param("displayName", "Operator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(10L))
                .andExpect(jsonPath("$.data.content[0].displayName").value("Operator One"));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}", 10L)
                        .with(authentication(mockAuth()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(userUseCase).deleteUser(1L, 10L);
    }
}
