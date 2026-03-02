package com.tcommerce.TCommerce.application.services.auth.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetRateLimit;
import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetToken;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.PasswordResetRateLimitRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.PasswordResetTokenRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;
import com.tcommerce.TCommerce.interfaces.dto.auth.ChangePasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.PasswordResetResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.ResetPasswordRequest;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetRateLimitRepository rateLimitRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private MailEventPublisher emailPublisher;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-id")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        
        ReflectionTestUtils.setField(passwordResetService, "domain", "http://localhost:8080");
    }

    @Test
    void requestPasswordReset_ShouldSendEmail_WhenValidRequest() {
        
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        PasswordResetRateLimit rateLimit = PasswordResetRateLimit.builder()
                .user(testUser)
                .attempts(0)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(rateLimitRepository.findByUser(testUser)).thenReturn(Optional.of(rateLimit));

        
        PasswordResetResponse response = passwordResetService.requestPasswordReset(request);

        
        assertThat(response.message()).isEqualTo("Email sent successfully.");
        assertThat(response.attemptsRemaining()).isEqualTo(4);
        
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailPublisher).buildAndPublish(eq("test@example.com"), anyString(), eq("password-reset"), any());
        verify(rateLimitRepository).save(any(PasswordResetRateLimit.class));
    }

    @Test
    void requestPasswordReset_ShouldReturnBlockedMessage_WhenTooManyAttempts() {
        
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com");
        PasswordResetRateLimit rateLimit = PasswordResetRateLimit.builder()
                .user(testUser)
                .attempts(5)
                .blockedUntil(LocalDateTime.now().plusMinutes(10))
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(rateLimitRepository.findByUser(testUser)).thenReturn(Optional.of(rateLimit));

        
        PasswordResetResponse response = passwordResetService.requestPasswordReset(request);

        
        assertThat(response.message()).isEqualTo("Too many attempts. Please try again after the block period.");
        assertThat(response.waitTimeSeconds()).isNotNull();
        
        verify(tokenRepository, times(0)).save(any());
    }

    @Test
    void resetPassword_ShouldUpdatePassword_WhenTokenIsValid() {
        
        String tokenString = "valid-token";
        ChangePasswordRequest request = new ChangePasswordRequest("new-password");
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        
        passwordResetService.resetPassword(tokenString, request);

        
        assertThat(testUser.getPassword()).isEqualTo("encoded-new-password");
        verify(userRepository).save(testUser);
        verify(tokenRepository).delete(token);
        verify(emailPublisher).buildAndPublish(eq("test@example.com"), anyString(), eq("password-changed"), any());
    }

    @Test
    void resetPassword_ShouldThrowException_WhenTokenIsExpired() {
        
        String tokenString = "expired-token";
        ChangePasswordRequest request = new ChangePasswordRequest("new-password");
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(testUser)
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        
        assertThatThrownBy(() -> passwordResetService.resetPassword(tokenString, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token expired");
    }
}
