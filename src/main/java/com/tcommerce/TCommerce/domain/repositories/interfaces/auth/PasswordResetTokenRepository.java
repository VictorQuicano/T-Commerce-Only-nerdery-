package com.tcommerce.TCommerce.domain.repositories.auth;

import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetToken;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import java.util.Optional;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void delete(PasswordResetToken token);
}
