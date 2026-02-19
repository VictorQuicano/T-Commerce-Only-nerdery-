package com.tcommerce.TCommerce.application.services.auth.impl;

import com.tcommerce.TCommerce.application.services.auth.PasswordResetService;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetRateLimit;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetToken;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.auth.PasswordResetTokenRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.PasswordResetRateLimitRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;
import com.tcommerce.TCommerce.interfaces.dto.auth.PasswordResetResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.ResetPasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRateLimitRepository rateLimitRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailEventPublisher emailPublisher;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_ATTEMPTS = 20;
    private static final int WAIT_MINUTES = 0;
    private static final int BLOCK_MINUTES = 20;

    @Value("${server.domain}")
    private String domain;

    @Override
    public PasswordResetResponse requestPasswordReset(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetRateLimit rateLimit = getRateLimit(user);
        LocalDateTime now = LocalDateTime.now();

        // 1. Check if Blocked
        if (rateLimit.getBlockedUntil() != null && now.isBefore(rateLimit.getBlockedUntil())) {
            long waitTime = Duration.between(now, rateLimit.getBlockedUntil()).toSeconds();
            return PasswordResetResponse.builder()
                    .message("Too many attempts. Please try again after the block period.")
                    .waitTimeSeconds(waitTime)
                    .build();
        }

        // Reset block if expired
        if (rateLimit.getBlockedUntil() != null && now.isAfter(rateLimit.getBlockedUntil())) {
            rateLimit.setBlockedUntil(null);
            rateLimit.setAttempts(0);
        }

        // 2. Check 5-minute wait window`
        if (rateLimit.getUpdatedAt() != null && now.isBefore(rateLimit.getUpdatedAt().plusMinutes(WAIT_MINUTES)) && rateLimit.getAttempts() > 0) {
            int remaining = MAX_ATTEMPTS - rateLimit.getAttempts();
            long waitTime = Duration.between(now, rateLimit.getUpdatedAt().plusMinutes(WAIT_MINUTES)).toSeconds();
            long waitMinutes = Duration.between(now, rateLimit.getUpdatedAt().plusMinutes(WAIT_MINUTES)).toMinutes();
            return PasswordResetResponse.builder()
                    .message("Please wait " + waitMinutes + " minutes between requests.")
                    .attemptsRemaining(remaining)
                    .waitTimeSeconds(waitTime)
                    .build();
        }

        // 3. Process Request
        rateLimit.setAttempts(rateLimit.getAttempts() + 1);
        rateLimit.setUpdatedAt(now);

        if (rateLimit.getAttempts() >= MAX_ATTEMPTS) {
            rateLimit.setBlockedUntil(now.plusMinutes(BLOCK_MINUTES));
        }

        rateLimitRepository.save(rateLimit);

        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(token);
        String resetUrl = domain + "/reset-password/" + tokenString;
        Map<String, String> dynamicBody = new HashMap<>(Map.of("reset_url", resetUrl));
        dynamicBody.put("domain", domain);
        dynamicBody.put("first_name", user.getFirstName());
        dynamicBody.put("last_name", user.getLastName());
        emailPublisher.buildAndPublish(user.getEmail(), "Password Reset Request","password-reset", dynamicBody);

        return PasswordResetResponse.builder()
                .message("Email sent successfully.")
                .attemptsRemaining(MAX_ATTEMPTS - rateLimit.getAttempts())
                .build();
    }

    private PasswordResetRateLimit getRateLimit(User user) {
        return rateLimitRepository.findByUser(user)
                .orElse(PasswordResetRateLimit.builder()
                        .user(user)
                        .attempts(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .blockedUntil(null)
                        .build());
    }

    @Override
    public void resetPassword(String tokenString, ChangePasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.isExpired()) {
            throw new RuntimeException("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        tokenRepository.delete(token);
    }

}
