package com.tcommerce.TCommerce.application.services.auth;

import com.tcommerce.TCommerce.interfaces.dto.auth.AuthResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.LoginRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.SignupRequest;

public interface AuthService {

    AuthResponse authenticate(LoginRequest loginRequest);

    AuthResponse registerUser(SignupRequest signUpRequest);
}
