package com.example.infrastructure.adapter.in.web;

import com.example.application.dto.UserRequestDto;
import com.example.application.dto.UserResponseDto;
import com.example.application.port.in.UserServicePort;
import com.example.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.infrastructure.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for creating, retrieving, updating and deleting users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserServicePort userServicePort;
    private final UserMapper userMapper;

    public UserController(UserServicePort userServicePort, UserMapper userMapper) {
        this.userServicePort = userServicePort;
        this.userMapper = userMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        log.info("REST request to create new user with email: {}", request.getEmail());
        User userDomain = userMapper.mapToDomain(request);
        User createdUser = userServicePort.createUser(userDomain);
        log.info("User created successfully with ID: {}", createdUser.getId());
        return new ResponseEntity<>(userMapper.mapToDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServicePort.getUserById(#id).email")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by ID: {}", id);
        User user = userServicePort.getUserById(id);
        return ResponseEntity.ok(userMapper.mapToDto(user));
    }

    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.info("REST request to retrieve all users list");
        List<UserResponseDto> users = userServicePort.getAllUsers().stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
        log.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user's details")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServicePort.getUserById(#id).email")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        log.info("REST request to update user with ID: {}", id);
        User userDetails = userMapper.mapToDomain(request);
        User updatedUser = userServicePort.updateUser(id, userDetails);
        log.info("User with ID: {} updated successfully", id);
        return ResponseEntity.ok(userMapper.mapToDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user with ID: {}", id);
        userServicePort.deleteUser(id);
        log.info("User with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}

