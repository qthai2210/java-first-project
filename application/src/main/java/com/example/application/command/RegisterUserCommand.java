package com.example.application.command;

public record RegisterUserCommand(
    String name,
    String email,
    String password
) {}