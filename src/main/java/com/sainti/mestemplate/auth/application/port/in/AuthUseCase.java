package com.sainti.mestemplate.auth.application.port.in;

import com.sainti.mestemplate.auth.application.dto.LoginCommand;
import com.sainti.mestemplate.auth.application.dto.LoginResult;

public interface AuthUseCase {

    LoginResult login(LoginCommand command);
}
