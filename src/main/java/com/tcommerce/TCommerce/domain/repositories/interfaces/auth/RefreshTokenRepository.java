
package com.tcommerce.TCommerce.domain.repositories.interfaces.auth;

import java.util.Optional;
import com.tcommerce.TCommerce.domain.entities.auth.RefreshToken;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void delete(RefreshToken refreshToken);
}
