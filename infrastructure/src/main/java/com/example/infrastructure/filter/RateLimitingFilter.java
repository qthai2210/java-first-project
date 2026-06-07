package com.example.infrastructure.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Filter that applies rate limiting to incoming REST API endpoints using Bucket4j.
 * Identifies clients using their authenticated username/email (if logged in) or client IP.
 * Excludes health check and Swagger/OpenAPI endpoints.
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final boolean enabled;
    private final int capacity;
    private final int refillTokens;
    private final int durationSeconds;
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public RateLimitingFilter(
            @Value("${app.rate-limit.enabled:true}") boolean enabled,
            @Value("${app.rate-limit.capacity:20}") int capacity,
            @Value("${app.rate-limit.refill-tokens:20}") int refillTokens,
            @Value("${app.rate-limit.duration-seconds:60}") int durationSeconds) {
        this.enabled = enabled;
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.durationSeconds = durationSeconds;
        log.info("Rate limiting initialized: enabled={}, capacity={}, refillTokens={}, durationSeconds={}",
                enabled, capacity, refillTokens, durationSeconds);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();

        // Exclude Swagger, API Docs, and public health checks from rate limiting
        if (uri.startsWith("/swagger-ui") || uri.contains("/v3/api-docs") || uri.equals("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = resolveClientKey(request);
        Bucket bucket = cache.computeIfAbsent(clientKey, key -> createNewBucket());

        if (bucket.tryConsume(1)) {
            response.addHeader("X-Rate-Limit-Limit", String.valueOf(capacity));
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for client: {}", clientKey);
            
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json;charset=UTF-8");

            // Calculate estimated seconds to wait until next token is available
            long nanosToWait = bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill();
            long secondsToWait = TimeUnit.NANOSECONDS.toSeconds(nanosToWait);
            if (secondsToWait <= 0) {
                secondsToWait = 1;
            }
            
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(secondsToWait));

            String jsonResponse = String.format(
                    "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"You have exceeded the rate limit. Please try again in %d seconds.\",\"timestamp\":\"%s\"}",
                    secondsToWait,
                    Instant.now().toString()
            );
            response.getWriter().write(jsonResponse);
        }
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(refillTokens, Duration.ofSeconds(durationSeconds));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientKey(HttpServletRequest request) {
        // 1. Try to identify by authenticated username/email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "user:" + auth.getName();
        }

        // 2. Fallback to client IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else if (ip.contains(",")) {
            // In case of multiple proxies, take the first client IP
            ip = ip.split(",")[0].trim();
        }
        return "ip:" + ip;
    }
}
