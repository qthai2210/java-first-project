package com.example.application.port.in;

import com.example.domain.model.User;

import java.util.List;

public interface UserServicePort {
    User createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
