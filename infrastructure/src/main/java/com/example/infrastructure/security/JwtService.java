package com.example.infrastructure.security;

import com.example.application.port.out.JwtServicePort;
import com.example.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT implementation using the JJWT library.
 * Lives in Infrastructure — the Application layer only sees JwtServicePort.
 */
@Slf4j
@Service
public class JwtService implements JwtServicePort {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @PostConstruct
    public void validateSecretKeyStrength() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT secret key (app.jwt.secret) must not be empty or null.");
        }
        
        int secretLength = secretKey.getBytes(StandardCharsets.UTF_8).length;
        if (secretLength < 32) {
            throw new IllegalStateException(
                "JWT secret key is too weak! It must be at least 32 bytes (256 bits) long. " +
                "Current length: " + secretLength + " bytes. Update the JWT_SECRET environment variable."
            );
        }

        if (secretKey.contains("development") || secretKey.contains("default") || secretKey.contains("test")) {
            log.warn("⚠️ SECURITY WARNING: You are using a default/development JWT secret key. " +
                     "Ensure the JWT_SECRET environment variable is securely set in production!");
        }
    }

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
