package com.example.infrastructure.mapper;

import com.example.infrastructure.adapter.in.web.dto.UserRequestDto;
import com.example.application.dto.UserResponseDto;
import com.example.domain.model.User;
import com.example.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User mapToDomain(UserRequestDto dto);

    UserResponseDto mapToDto(User domain);

    UserJpaEntity mapToJpaEntity(User domain);

    User mapToDomain(UserJpaEntity entity);
}
