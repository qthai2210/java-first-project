package com.example.application.service;

import com.example.application.port.in.UserServicePort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResourceNotFoundException;
import com.example.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserService implements UserServicePort {

    private final UserPersistencePort userPersistencePort;

    public UserService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public User createUser(User user) {
        if (userPersistencePort.existsByEmail(user.getEmail())) {
            throw new DomainException("Email '" + user.getEmail() + "' is already in use");
        }
        user.setCreatedAt(LocalDateTime.now());
        return userPersistencePort.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userPersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userPersistencePort.findAll();
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(userDetails.getEmail()) 
                && userPersistencePort.existsByEmail(userDetails.getEmail())) {
            throw new DomainException("Email '" + userDetails.getEmail() + "' is already in use");
        }

        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());

        return userPersistencePort.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userPersistencePort.existsById(id)) {
            throw new ResourceNotFoundException("User with ID " + id + " not found");
        }
        userPersistencePort.deleteById(id);
    }
}
