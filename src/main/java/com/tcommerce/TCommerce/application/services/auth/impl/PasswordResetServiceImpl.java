package com.tcommerce.TCommerce.application.services.auth.impl;

import com.tcommerce.TCommerce.application.services.auth.PasswordResetService;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetRateLimit;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetToken;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.auth.PasswordResetTokenRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.PasswordResetRateLimitRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.services.EmailService;
import com.tcommerce.TCommerce.interfaces.dto.auth.ChangePasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRateLimitRepository rateLimitRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void requestPasswordReset(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkRateLimit(user);

        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(token);

        String resetUrl = "{domain}/reset-password/" + tokenString;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", "Click here to reset your password: " + resetUrl);
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

    private void checkRateLimit(User user) {
        PasswordResetRateLimit rateLimit = rateLimitRepository.findByUser(user)
                .orElse(PasswordResetRateLimit.builder()
                        .user(user)
                        .attempts(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

        LocalDateTime now = LocalDateTime.now();

        if (rateLimit.getBlockedUntil() != null && now.isBefore(rateLimit.getBlockedUntil())) {
            throw new RuntimeException("Too many requests. Please try again later.");
        }

        if (rateLimit.getBlockedUntil() != null && now.isAfter(rateLimit.getBlockedUntil())) {
            // Unblock
            rateLimit.setBlockedUntil(null);
            rateLimit.setAttempts(0);
        }
        
        // Enforcing 5 minute window between requests
        if (rateLimit.getUpdatedAt() != null && now.isBefore(rateLimit.getUpdatedAt().plusMinutes(5))) {
             if (rateLimit.getAttempts() < 5) {
                 throw new RuntimeException("Please wait 5 minutes between requests.");
             }
        }
        
        rateLimit.setAttempts(rateLimit.getAttempts() + 1);
        rateLimit.setUpdatedAt(now);
        
        if (rateLimit.getAttempts() >= 5) {
            rateLimit.setBlockedUntil(now.plusMinutes(20));
            // Reset attempts or keep them? Usually reset after penalty.
            // But if we reset, they can start again immediately after 20 mins.
            // Logic: attempts >= 5 -> Block 20 mins.
        }
        
        rateLimitRepository.save(rateLimit);
    }
}
