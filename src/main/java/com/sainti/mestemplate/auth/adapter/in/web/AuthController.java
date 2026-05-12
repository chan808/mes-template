package com.sainti.mestemplate.auth.adapter.in.web;

import com.sainti.mestemplate.auth.adapter.in.web.dto.LoginRequest;
import com.sainti.mestemplate.auth.adapter.in.web.dto.LoginResponse;
import com.sainti.mestemplate.auth.application.dto.LoginCommand;
import com.sainti.mestemplate.auth.application.port.in.AuthUseCase;
import com.sainti.mestemplate.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.tenantId(), request.loginId(), request.password());
        return ApiResponse.success(LoginResponse.from(authUseCase.login(command)));
    }
}
