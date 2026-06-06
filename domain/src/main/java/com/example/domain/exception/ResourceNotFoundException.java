package com.example.domain.exception;

import com.example.domain.exception.DomainException;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
