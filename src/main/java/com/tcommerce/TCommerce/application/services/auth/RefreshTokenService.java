package com.tcommerce.TCommerce.application.services.auth;

import com.tcommerce.TCommerce.domain.entities.auth.RefreshToken;
import java.util.Optional;
import com.tcommerce.TCommerce.interfaces.dto.auth.RefreshTokenRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.RefreshTokenResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;


public interface RefreshTokenService {

    RefreshToken createRefreshToken(String userId);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    RefreshTokenResponse generateNewToken(RefreshTokenRequest request);
    ResponseCookie generateRefreshTokenCookie(String token);
    String getRefreshTokenFromCookies(HttpServletRequest request);
    void deleteByToken(String token);
    ResponseCookie getCleanRefreshTokenCookie();
}
