package com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.PasswordResetRateLimitEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;

@Repository
public interface JpaPasswordResetRateLimitRepository
        extends JpaRepository<PasswordResetRateLimitEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT r 
           FROM PasswordResetRateLimitEntity r 
           WHERE r.user = :user
           """)
    Optional<PasswordResetRateLimitEntity> findByUserForUpdate(
            @Param("user") UserEntity user
    );

    Optional<PasswordResetRateLimitEntity> findByUser(UserEntity user);
}