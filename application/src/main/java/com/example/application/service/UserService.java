package com.example.application.service;

import com.example.application.port.in.UserServicePort;
import com.example.application.port.out.PasswordEncoderPort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.Role;
import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.User;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class UserService implements UserServicePort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserService(UserPersistencePort userPersistencePort, PasswordEncoderPort passwordEncoderPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User createUser(User user) {
        log.debug("Creating user with email: {}", user.getEmail());
        if (userPersistencePort.existsByEmail(user.getEmail())) {
            log.warn("Email registration failed: '{}' is already in use", user.getEmail());
            throw new DomainException("Email '" + user.getEmail() + "' is already in use");
        }
        
        Role role = user.getRole() != null ? user.getRole() : Role.USER;
        String encodedPassword = user.getPassword() != null ? passwordEncoderPort.encode(user.getPassword()) : null;
        
        User userToSave = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(encodedPassword)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
                
        User savedUser = userPersistencePort.save(userToSave);
        log.info("User registered successfully in database with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public User getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userPersistencePort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found", id);
                    return new ResourceNotFoundException("User with ID " + id + " not found");
                });
    }

    @Override
    public PageDataDto<User> getAllUsers(PageQueryDto query) {
        log.debug("Fetching all users list with page {} size {}", query.getPage(), query.getSize());
        return userPersistencePort.findAll(query);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        log.debug("Updating user details for ID: {}", id);
        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(userDetails.getEmail()) 
                && userPersistencePort.existsByEmail(userDetails.getEmail())) {
            log.warn("Failed to update user ID: {}: Email '{}' is already in use", id, userDetails.getEmail());
            throw new DomainException("Email '" + userDetails.getEmail() + "' is already in use");
        }

        User.UserBuilder userBuilder = existingUser.toBuilder()
                .name(userDetails.getName())
                .email(userDetails.getEmail());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            userBuilder.password(passwordEncoderPort.encode(userDetails.getPassword()));
        }
        
        if (userDetails.getRole() != null) {
            userBuilder.role(userDetails.getRole());
        }

        User updatedUser = userPersistencePort.save(userBuilder.build());
        log.info("User with ID: {} updated successfully in database", id);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        if (!userPersistencePort.existsById(id)) {
            log.warn("Deletion failed: User with ID: {} not found", id);
            throw new ResourceNotFoundException("User with ID " + id + " not found");
        }
        userPersistencePort.deleteById(id);
        log.info("User with ID: {} deleted successfully from database", id);
    }
}
