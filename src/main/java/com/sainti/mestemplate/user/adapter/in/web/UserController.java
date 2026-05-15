package com.sainti.mestemplate.user.adapter.in.web;

import com.sainti.mestemplate.global.response.ApiResponse;
import com.sainti.mestemplate.global.security.MesPrincipal;
import com.sainti.mestemplate.user.adapter.in.web.dto.ChangeMyPasswordRequest;
import com.sainti.mestemplate.user.adapter.in.web.dto.ResetPasswordResponse;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserCreateRequest;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserResponse;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserSearch;
import com.sainti.mestemplate.user.adapter.in.web.dto.UserUpdateRequest;
import com.sainti.mestemplate.user.application.dto.ChangeMyPasswordCommand;
import com.sainti.mestemplate.user.application.dto.CreateUserCommand;
import com.sainti.mestemplate.user.application.dto.DeleteUserCommand;
import com.sainti.mestemplate.user.application.dto.ResetPasswordCommand;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(userUseCase.createUser(command))));
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
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(userUseCase.updateUser(command))));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                UserResponse.from(userUseCase.getUser(principal.tenantId(), userId))
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @AuthenticationPrincipal MesPrincipal principal,
            UserSearch search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UserQuery query = new UserQuery(
                search.loginId(), search.displayName(), search.role(), search.status()
        );
        Page<UserResponse> response = userUseCase.searchUsers(principal.tenantId(), query, pageable)
                .map(UserResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long userId
    ) {
        userUseCase.deleteUser(new DeleteUserCommand(principal.tenantId(), userId, principal.userId()));
        return ResponseEntity.ok(ApiResponse.successVoid());
    }

    // TENANT_ADMIN 전용 — 대상 사용자의 임시 비밀번호 발급, mustChangePassword = true 설정
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @AuthenticationPrincipal MesPrincipal principal,
            @PathVariable Long userId
    ) {
        ResetPasswordCommand command = new ResetPasswordCommand(
                principal.tenantId(), userId, principal.userId()
        );
        return ResponseEntity.ok(ApiResponse.success(
                ResetPasswordResponse.from(userUseCase.resetPassword(command))
        ));
    }

    // 인증된 사용자 본인 — mustChangePassword = true 상태에서도 허용
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            @AuthenticationPrincipal MesPrincipal principal,
            @Valid @RequestBody ChangeMyPasswordRequest request
    ) {
        ChangeMyPasswordCommand command = new ChangeMyPasswordCommand(
                principal.tenantId(),
                principal.userId(),
                request.currentPassword(),
                request.newPassword()
        );
        userUseCase.changeMyPassword(command);
        return ResponseEntity.ok(ApiResponse.successVoid());
    }
}
