package com.tcommerce.TCommerce.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.RefreshTokenEntity;

import java.util.Optional;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
}
