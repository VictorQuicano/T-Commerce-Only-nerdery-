package com.tcommerce.TCommerce.domain.repositories.interfaces.auth;

import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetRateLimit;
import com.tcommerce.TCommerce.domain.entities.auth.User;

@Repository
public interface PasswordResetRateLimitRepository {
    Optional<PasswordResetRateLimit> findByUser(User user);
    PasswordResetRateLimit save(PasswordResetRateLimit passwordResetRateLimit);
}
