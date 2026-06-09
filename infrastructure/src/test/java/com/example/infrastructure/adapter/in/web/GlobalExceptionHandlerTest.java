package com.example.infrastructure.adapter.in.web;

import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleResourceNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("User with ID 123 not found");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResourceNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("User with ID 123 not found", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testHandleDomainExceptionWithRegularMessage() {
        DomainException exception = new DomainException("Simple domain business rule violated");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDomainException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Simple domain business rule violated", response.getBody().get("message"));
    }

    @Test
    void testHandleDomainExceptionSanitizesEmail() {
        DomainException exception = new DomainException("Email user.test_123+spam@sub.domain-name.co.uk is already registered");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDomainException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email [EMAIL_REDACTED] is already registered", response.getBody().get("message"));
    }

    @Test
    void testHandleDomainExceptionSanitizesDatabaseConstraint() {
        DomainException exception = new DomainException("Failed because of UNIQUE CONSTRAINT violation on FOREIGN KEY user_id");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDomainException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed because of [DB_REDACTED] [DB_REDACTED] on [DB_REDACTED] user_id", response.getBody().get("message"));
    }

    @Test
    void testHandleDomainExceptionSanitizesClassNameAndPackage() {
        DomainException exception = new DomainException("Error in com.example.domain.model.User: com.example.infrastructure.MyClass has failed");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDomainException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error in [CLASS_REDACTED]: [CLASS_REDACTED] has failed", response.getBody().get("message"));
    }

    @Test
    void testHandleAuthenticationException() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials provided");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Unauthorized", response.getBody().get("error"));
        assertEquals("Authentication failed", response.getBody().get("message"));
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Forbidden action");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Forbidden", response.getBody().get("error"));
        assertEquals("Access denied: You do not have permission to perform this action", response.getBody().get("message"));
    }

    @Test
    void testHandleGenericException() {
        NullPointerException exception = new NullPointerException("Cannot invoke \"Object.toString()\" because \"obj\" is null");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
    }

    @Test
    void testHandleValidationException() {
        // Mock MethodArgumentNotValidException
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("userDto", "email", "Email is invalid");
        FieldError fieldError2 = new FieldError("userDto", "name", "Name cannot be blank");
        
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Validation failed", response.getBody().get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Email is invalid", errors.get("email"));
        assertEquals("Name cannot be blank", errors.get("name"));
    }
}
