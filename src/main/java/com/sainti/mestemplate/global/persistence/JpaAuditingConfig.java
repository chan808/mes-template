package com.sainti.mestemplate.global.persistence;

import com.sainti.mestemplate.global.security.MesPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing 설정.
 *
 * <p>SecurityContext에서 현재 인증된 사용자의 userId를 추출하여
 * {@code @CreatedBy}, {@code @LastModifiedBy} 필드에 자동 주입합니다.
 *
 * <p>인증 정보가 없는 경우(예: 시스템 배치 작업) {@code Optional.empty()}를 반환하여
 * audit 필드가 null로 저장됩니다.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(MesPrincipal.class::isInstance)
                .map(MesPrincipal.class::cast)
                .map(MesPrincipal::userId);
    }
}
