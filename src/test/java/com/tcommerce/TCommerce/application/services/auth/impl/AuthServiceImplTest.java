package com.tcommerce.TCommerce.application.services.auth.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tcommerce.TCommerce.application.services.auth.JwtService;
import com.tcommerce.TCommerce.application.services.auth.RefreshTokenService;
import com.tcommerce.TCommerce.application.services.auth.WelcomeNotificationService;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.interfaces.dto.auth.AuthResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.LoginRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.SignupRequest;
import com.tcommerce.TCommerce.domain.entities.auth.RefreshToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private WelcomeNotificationService welcomeNotificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-id")
                .email("test@example.com")
                .password("encoded-password")
                .firstName("John")
                .lastName("Doe")
                .build();

        testRefreshToken = RefreshToken.builder()
                .token("refresh-token")
                .build();
    }

    @Test
    void authenticate_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(testRefreshToken);

        
        AuthResponse response = authService.authenticate(loginRequest);

        
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void registerUser_ShouldReturnAuthResponse_WhenSignupIsSuccessful() {
        
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(testRefreshToken);

        
        AuthResponse response = authService.registerUser(signupRequest);

        
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
        verify(welcomeNotificationService).notifyUser(anyString(), anyString(), anyString());
    }
}
