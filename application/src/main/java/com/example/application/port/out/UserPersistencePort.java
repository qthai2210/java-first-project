package com.example.application.port.out;

import com.example.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
}
