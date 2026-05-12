package com.sainti.mestemplate.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public record TokenPayload(Long userId, Long tenantId, String role) {}

    public TokenPayload parseToken(String token) {
        Claims claims = parseClaims(token);

        String subject = claims.getSubject();
        Long tenantId = claims.get("tenantId", Long.class);
        String role = claims.get("role", String.class);

        if (!StringUtils.hasText(subject) || tenantId == null || !StringUtils.hasText(role)) {
            throw new IllegalArgumentException("JWT is missing required claims");
        }

        return new TokenPayload(Long.valueOf(subject), tenantId, role);
    }

    public String generateToken(Long userId, Long tenantId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("tenantId", tenantId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public Long getTenantId(String token) {
        return parseClaims(token).get("tenantId", Long.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
