package com.tcommerce.TCommerce.application.services.auth.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tcommerce.TCommerce.application.enums.TokenType;
import com.tcommerce.TCommerce.application.services.auth.AuthService;
import com.tcommerce.TCommerce.application.services.auth.JwtService;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.interfaces.dto.auth.AuthResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.LoginRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.SignupRequest;

import lombok.RequiredArgsConstructor;

import com.tcommerce.TCommerce.application.services.auth.RefreshTokenService;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;
import java.util.UUID;  
import java.util.ArrayList;
import com.tcommerce.TCommerce.application.services.auth.WelcomeNotificationService;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final WelcomeNotificationService welcomeNotificationService;
    
    @Override
    public AuthResponse authenticate(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        var jwt = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return AuthResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .id(user.getId())
                .refreshToken(refreshToken.getToken())
                .tokenType( TokenType.BEARER.name())
                .build();
    }

    @Override
    public AuthResponse registerUser(SignupRequest request){
        var user = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ERole.CLIENT)
                .build();
        user = userRepository.save(user);
        welcomeNotificationService.notifyUser(user.getEmail(), user.getFirstName(), user.getLastName());
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        var jwt = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .id(user.getId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .refreshToken(refreshToken.getToken())
                .tokenType(TokenType.BEARER.name())
                .build();
    }
}
