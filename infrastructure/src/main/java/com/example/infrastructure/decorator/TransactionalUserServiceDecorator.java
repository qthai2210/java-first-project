package com.example.infrastructure.decorator;

import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.application.port.in.UserServicePort;
import com.example.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalUserServiceDecorator implements UserServicePort {

    private final UserServicePort delegate;

    public TransactionalUserServiceDecorator(UserServicePort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return delegate.createUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return delegate.getUserById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return delegate.getUserByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDataDto<User> getAllUsers(PageQueryDto query) {
        return delegate.getAllUsers(query);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        return delegate.updateUser(id, user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        delegate.deleteUser(id);
    }
}
