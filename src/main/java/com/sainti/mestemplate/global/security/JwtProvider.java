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

    // JWT에는 userId, tenantId만 — role/status는 매 요청마다 DB에서 조회
    public record TokenPayload(Long userId, Long tenantId) {}

    public TokenPayload parseToken(String token) {
        Claims claims = parseClaims(token);

        String subject = claims.getSubject();
        Long tenantId = claims.get("tenantId", Long.class);

        if (!StringUtils.hasText(subject) || tenantId == null) {
            throw new IllegalArgumentException("JWT is missing required claims");
        }

        return new TokenPayload(Long.valueOf(subject), tenantId);
    }

    public String generateToken(Long userId, Long tenantId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("tenantId", tenantId)
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
}
