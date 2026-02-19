package com.tcommerce.TCommerce.infrastructure.persistence.mappers.auth;

import org.springframework.stereotype.Component;
import com.tcommerce.TCommerce.domain.entities.auth.RefreshToken;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.RefreshTokenEntity;

@Component
public class RefreshTokenMapper {

    private final UserMapper userMapper;
    
    public RefreshTokenMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public RefreshToken toDomain(RefreshTokenEntity entity){
        if (entity == null) return null;

        return RefreshToken.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .user(userMapper.toDomain(entity.getUser()))
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }

    public RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        if (refreshToken == null) return null;

        return RefreshTokenEntity.builder()
                .id(refreshToken.getId())
                .token(refreshToken.getToken())
                .user(userMapper.toEntity(refreshToken.getUser()))
                .expiresAt(refreshToken.getExpiresAt())
                .revokedAt(refreshToken.getRevokedAt())
                .build();
    }

}
