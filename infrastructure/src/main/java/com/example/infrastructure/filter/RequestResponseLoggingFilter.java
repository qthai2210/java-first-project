package com.example.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that logs incoming HTTP requests and outgoing HTTP responses.
 * Excludes Swagger UI and OpenAPI documentation requests to avoid log noise.
 */
@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Skip logging for Swagger UI and OpenAPI docs
        if (uri.startsWith("/swagger-ui") || uri.contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String clientIp = request.getRemoteAddr();
        String fullPath = queryString != null ? uri + "?" + queryString : uri;

        log.info("Incoming HTTP Request: Method=[{}], Path=[{}], ClientIP=[{}]", method, fullPath, clientIp);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            log.info("Outgoing HTTP Response: Method=[{}], Path=[{}], Status=[{}], ExecutionTime=[{}ms]", 
                    method, uri, status, duration);
        }
    }
}
