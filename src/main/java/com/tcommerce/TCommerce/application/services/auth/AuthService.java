package com.tcommerce.TCommerce.application.services.auth;

import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;
import com.tcommerce.TCommerce.infrastructure.security.jwt.JwtUtils;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.auth.AuthResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.LoginRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                ERole.valueOf(userDetails.getAuthorities().iterator().next().getAuthority())
        );
    }

    public void registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(
                UUID.randomUUID().toString(),
                signUpRequest.getFirstName(),
                signUpRequest.getMiddleName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                ERole.CLIENT,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        userRepository.save(user);
    }
}
