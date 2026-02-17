package com.tcommerce.TCommerce.domain.repositories.implementations.auth;

import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaRefreshTokenRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.auth.RefreshTokenMapper;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import com.tcommerce.TCommerce.domain.entities.auth.RefreshToken;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository{

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;
    
    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRefreshTokenRepository, RefreshTokenMapper refreshTokenMapper) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }
    
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .map(refreshTokenMapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenMapper.toDomain(jpaRefreshTokenRepository.save(refreshTokenMapper.toEntity(refreshToken)));
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        jpaRefreshTokenRepository.delete(refreshTokenMapper.toEntity(refreshToken));
    }
}
