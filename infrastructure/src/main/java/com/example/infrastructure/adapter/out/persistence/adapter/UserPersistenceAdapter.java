package com.example.infrastructure.adapter.out.persistence.adapter;

import com.example.application.port.out.UserPersistencePort;
import com.example.application.dto.PageDataDto;
import com.example.application.dto.PageQueryDto;
import com.example.domain.model.User;
import com.example.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.example.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.example.infrastructure.mapper.PaginationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import com.example.infrastructure.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = userMapper.mapToJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return userMapper.mapToDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::mapToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userMapper::mapToDomain);
    }

    @Override
    public PageDataDto<User> findAll(PageQueryDto query) {
        Sort.Direction direction = Sort.Direction.fromString(query.getSortDirection());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(direction, query.getSortBy()));
        Page<UserJpaEntity> page = userJpaRepository.findAll(pageable);
        return PaginationMapper.map(page, userMapper::mapToDomain);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
