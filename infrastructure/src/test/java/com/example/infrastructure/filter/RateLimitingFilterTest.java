package com.example.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    private RateLimitingFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize filter with capacity = 2, refill = 2 tokens per 60 seconds
        filter = new RateLimitingFilter(true, 2, 2, 60);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);

        // Default remote IP and URI
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/users");
    }

    @Test
    void testRequestWithinLimit() throws ServletException, IOException {
        // First request should pass
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, times(1)).addHeader("X-Rate-Limit-Limit", "2");
        verify(response, times(1)).addHeader("X-Rate-Limit-Remaining", "1");

        // Second request should pass
        filter.doFilter(request, response, filterChain);
        verify(filterChain, times(2)).doFilter(request, response);
        verify(response, times(1)).addHeader("X-Rate-Limit-Remaining", "0");
    }

    @Test
    void testRequestExceedingLimitReturns429() throws ServletException, IOException {
        // Consume both tokens
        filter.doFilter(request, response, filterChain); // remaining: 1
        filter.doFilter(request, response, filterChain); // remaining: 0

        // Third request should exceed the limit and return 429
        filter.doFilter(request, response, filterChain);

        // Verify filter chain was only invoked twice, not three times
        verify(filterChain, times(2)).doFilter(request, response);

        // Verify status and response content
        verify(response, times(1)).setStatus(429);
        verify(response, times(1)).setContentType("application/json;charset=UTF-8");
        verify(response, times(1)).addHeader(eq("X-Rate-Limit-Retry-After-Seconds"), anyString());

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"status\":429"));
        assertTrue(jsonResponse.contains("Too Many Requests"));
    }

    @Test
    void testExcludedEndpointsAreNotRateLimited() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // Execute 5 requests
        for (int i = 0; i < 5; i++) {
            filter.doFilter(request, response, filterChain);
        }

        // All 5 requests should pass to filter chain since health is excluded
        verify(filterChain, times(5)).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void testDisabledFilterAllowsAll() throws ServletException, IOException {
        // Initialize disabled filter
        filter = new RateLimitingFilter(false, 2, 2, 60);

        // Execute 5 requests
        for (int i = 0; i < 5; i++) {
            filter.doFilter(request, response, filterChain);
        }

        // All should pass since filter is disabled
        verify(filterChain, times(5)).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }
}
