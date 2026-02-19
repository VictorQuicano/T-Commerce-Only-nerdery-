package com.tcommerce.TCommerce.domain.repositories.implementations.auth;


import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.PasswordResetRateLimitRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.auth.PasswordResetRateLimitMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.auth.UserMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth.JpaPasswordResetRateLimitRepository;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetRateLimit;
import com.tcommerce.TCommerce.domain.entities.auth.User;

@Repository
@RequiredArgsConstructor
public class PasswordResetRateLimitRepositoryImpl implements PasswordResetRateLimitRepository{
    private final JpaPasswordResetRateLimitRepository jpaRepo;
    private final UserMapper userMapper;
    private final PasswordResetRateLimitMapper resetPasswordRateLimitMapper;

    @Override
    public Optional<PasswordResetRateLimit> findByUser(User user) {
        return jpaRepo.findByUser(userMapper.toEntity(user))
                .map(resetPasswordRateLimitMapper::toDomain);
    }

    @Override
    public PasswordResetRateLimit save(PasswordResetRateLimit resetPasswordRateLimit) {
        return resetPasswordRateLimitMapper.toDomain(jpaRepo.save(resetPasswordRateLimitMapper.toEntity(resetPasswordRateLimit)));
    }

}
