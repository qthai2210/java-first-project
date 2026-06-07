package com.example.application.port.out;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    PageDataDto<User> findAll(PageQueryDto query);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
}
