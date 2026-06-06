package com.example.infrastructure.adapter.out.persistence.repository;

import com.example.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    boolean existsByEmail(String email);
}
