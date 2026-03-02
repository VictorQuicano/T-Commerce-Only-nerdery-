package com.tcommerce.TCommerce.application.services.auth.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.tcommerce.TCommerce.application.services.auth.JwtService;
import com.tcommerce.TCommerce.domain.entities.auth.RefreshToken;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.exceptions.TokenException;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.RefreshTokenRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.interfaces.dto.auth.RefreshTokenRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.RefreshTokenResponse;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-id")
                .email("test@example.com")
                .password("test-password")
                .build();

        testRefreshToken = RefreshToken.builder()
                .token("valid-refresh-token")
                .user(testUser)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        ReflectionTestUtils.setField(refreshTokenService, "refreshExpiration", 3600000L);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenName", "refresh-token");
    }

    @Test
    void createRefreshToken_ShouldSaveAndReturnToken() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken("user-id");

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("valid-refresh-token");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ShouldReturnToken_WhenNotExpired() {
        RefreshToken result = refreshTokenService.verifyExpiration(testRefreshToken);
        assertThat(result).isEqualTo(testRefreshToken);
    }

    @Test
    void verifyExpiration_ShouldThrowException_WhenExpired() {
        testRefreshToken.setExpiresAt(Instant.now().minusSeconds(1));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(testRefreshToken))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining("expired");
        
        verify(refreshTokenRepository).delete(testRefreshToken);
    }

    @Test
    void generateNewToken_ShouldReturnResponse_WhenValid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(testRefreshToken));
        when(jwtService.generateToken(any())).thenReturn("new-access-token");

        RefreshTokenResponse response = refreshTokenService.generateNewToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("valid-refresh-token");
    }
}
