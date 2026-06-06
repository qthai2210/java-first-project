package com.example.infrastructure.mapper;

import com.example.domain.model.RefreshToken;
import com.example.infrastructure.adapter.out.persistence.entity.RefreshTokenJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefreshTokenMapper {

    RefreshTokenJpaEntity mapToJpaEntity(RefreshToken domain);

    RefreshToken mapToDomain(RefreshTokenJpaEntity entity);
}
