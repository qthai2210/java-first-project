package com.example.infrastructure.adapter.in.web;

import com.example.application.dto.UserRequestDto;
import com.example.application.dto.UserResponseDto;
import com.example.application.port.in.UserServicePort;
import com.example.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for creating, retrieving, updating and deleting users")
public class UserController {

    private final UserServicePort userServicePort;

    public UserController(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto request) {
        User userDomain = mapToDomain(request);
        User createdUser = userServicePort.createUser(userDomain);
        return new ResponseEntity<>(mapToDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userServicePort.getUserById(id);
        return ResponseEntity.ok(mapToDto(user));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userServicePort.getAllUsers().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user's details")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto request) {
        User userDetails = mapToDomain(request);
        User updatedUser = userServicePort.updateUser(id, userDetails);
        return ResponseEntity.ok(mapToDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userServicePort.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private User mapToDomain(UserRequestDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    private UserResponseDto mapToDto(User domain) {
        return UserResponseDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
