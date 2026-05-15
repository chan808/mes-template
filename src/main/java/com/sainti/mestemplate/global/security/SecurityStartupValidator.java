package com.sainti.mestemplate.global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SecurityStartupValidator implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (activeProfile.contains("prod")) {
            if (jwtSecret.contains("mestemplate")) {
                throw new IllegalStateException(
                        "[보안] prod 환경에서 기본 JWT 시크릿 사용 금지. JWT_SECRET 환경변수를 설정하세요."
                );
            }
        }
    }
}
