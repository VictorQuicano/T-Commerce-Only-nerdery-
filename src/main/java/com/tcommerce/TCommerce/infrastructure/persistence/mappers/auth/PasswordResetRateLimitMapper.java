package com.tcommerce.TCommerce.infrastructure.persistence.mappers.auth;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetRateLimit;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.PasswordResetRateLimitEntity;

@Component
@RequiredArgsConstructor
public class PasswordResetRateLimitMapper {
    private final UserMapper userMapper;
    public PasswordResetRateLimitEntity toEntity(PasswordResetRateLimit resetPasswordRateLimit) {
        if(resetPasswordRateLimit == null) return null;
        return PasswordResetRateLimitEntity.builder()
                .id(resetPasswordRateLimit.getId())
                .user(userMapper.toEntity(resetPasswordRateLimit.getUser()))
                .attempts(resetPasswordRateLimit.getAttempts())
                .blockedUntil(resetPasswordRateLimit.getBlockedUntil())
                .createdAt(resetPasswordRateLimit.getCreatedAt())
                .updatedAt(resetPasswordRateLimit.getUpdatedAt())
                .build();
    }

    public PasswordResetRateLimit toDomain(PasswordResetRateLimitEntity resetPasswordRateLimitEntity) {
        if(resetPasswordRateLimitEntity == null) return null;
        return PasswordResetRateLimit.builder()
                .id(resetPasswordRateLimitEntity.getId())
                .user(userMapper.toDomain(resetPasswordRateLimitEntity.getUser()))
                .attempts(resetPasswordRateLimitEntity.getAttempts())
                .blockedUntil(resetPasswordRateLimitEntity.getBlockedUntil())
                .createdAt(resetPasswordRateLimitEntity.getCreatedAt())
                .updatedAt(resetPasswordRateLimitEntity.getUpdatedAt())
                .build();
    }
}
