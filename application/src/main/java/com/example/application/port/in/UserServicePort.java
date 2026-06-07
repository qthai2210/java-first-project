package com.example.application.port.in;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.User;

import java.util.List;

public interface UserServicePort {
    User createUser(User user);
    User getUserById(Long id);
    PageDataDto<User> getAllUsers(PageQueryDto query);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
