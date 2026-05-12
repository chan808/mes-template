package com.sainti.mestemplate.user.adapter.in.web;

import com.sainti.mestemplate.global.response.ApiResponse;
import com.sainti.mestemplate.global.security.MesPrincipal;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserCreateRequest;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserResponse;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserSearch;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserUpdateRequest;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.UpdateUserCommand;
import com.sainti.mestemplate.user.application.dto.UserQuery;
import com.sainti.mestemplate.user.application.port.in.UserUseCase;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @AuthenticationPrincipal MesPrincipal principal,
            @Valid @RequestBody UserCreateRequest request
    ) {
        CreateUserCommand command = new CreateUserCommand(
                principal.tenantId(),
                request.loginId(),
                request.password(),
                request.displayName(),
                request.role()
        );

        UserResponse response = UserResponse.from(userUseCase.createUser(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UpdateUserCommand command = new UpdateUserCommand(
                principal.tenantId(),
                userId,
                request.displayName(),
                request.role(),
                request.status()
        );

        UserResponse response = UserResponse.from(userUseCase.updateUser(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long userId
    ) {
        UserResponse response = UserResponse.from(
                userUseCase.getUser(principal.tenantId(), userId));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @AuthenticationPrincipal MesPrincipal principal,
            UserSearch search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UserQuery query = new UserQuery(
                search.loginId(),
                search.displayName(),
                search.role(),
                search.status()
        );

        Page<UserResponse> response = userUseCase.searchUsers(
                        principal.tenantId(), query, pageable)
                .map(UserResponse::from);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long userId
    ) {
        userUseCase.deleteUser(principal.tenantId(), userId);

        return ResponseEntity.ok(ApiResponse.successVoid());
    }
}
