package com.example.infrastructure.security;

import com.example.application.port.out.JwtServicePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter that runs once per request.
 * Extracts the Bearer token from the Authorization header,
 * validates it, and sets the Spring Security context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtServicePort jwtServicePort;
    private final UserPersistencePort userPersistencePort;

    public JwtAuthenticationFilter(JwtServicePort jwtServicePort,
                                   UserPersistencePort userPersistencePort) {
        this.jwtServicePort = jwtServicePort;
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Skip filter if no Bearer token present
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX.length());
        final String userEmail;

        try {
            userEmail = jwtServicePort.extractEmail(jwt);
        } catch (Exception e) {
            // Invalid token format — let Spring Security handle it as UNAUTHORIZED
            filterChain.doFilter(request, response);
            return;
        }

        // Only set auth if user is not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userPersistencePort.findByEmail(userEmail).orElse(null);

            if (user != null && jwtServicePort.isTokenValid(jwt, user)) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                        .build();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
