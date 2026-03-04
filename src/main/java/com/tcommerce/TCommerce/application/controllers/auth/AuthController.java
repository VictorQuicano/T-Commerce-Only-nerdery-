package com.tcommerce.TCommerce.application.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.auth.AuthService;
import com.tcommerce.TCommerce.application.services.auth.RefreshTokenService;
import com.tcommerce.TCommerce.application.services.auth.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import com.tcommerce.TCommerce.interfaces.dto.auth.AuthResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.LoginRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.RefreshTokenRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.RefreshTokenResponse;
import com.tcommerce.TCommerce.interfaces.dto.auth.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.V1 +"/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management")
public class AuthController {

    private final AuthService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns access and refresh tokens.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", 
                         content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignupRequest request) {
        AuthResponse authenticationResponse = authenticationService.registerUser(request);
        ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
        ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .body(authenticationResponse);
    }

    @Operation(
        summary = "User login",
        description = "Authenticates a user and returns access and refresh tokens via body and cookies.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", 
                         content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
        }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        AuthResponse authenticationResponse = authenticationService.authenticate(request);
        ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
        ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .body(authenticationResponse);
    }
    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using a valid refresh token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", 
                         content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Refresh token is expired or invalid")
        }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }

    @Operation(
        summary = "Refresh access token via cookie",
        description = "Generates a new access token using the refresh token stored in cookies.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "403", description = "Refresh token in cookie is missing or invalid")
        }
    )
    @PostMapping("/refresh-token-cookie")
    public ResponseEntity<Void> refreshTokenCookie(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService
                .generateNewToken(new RefreshTokenRequest(refreshToken));
        ResponseCookie NewJwtCookie = jwtService.generateJwtCookie(refreshTokenResponse.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, NewJwtCookie.toString())
                .build();
    }
    @GetMapping("/info")
    public Authentication getAuthentication(@RequestBody LoginRequest request){
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
    }

    @Operation(
        summary = "User logout",
        description = "Invalidates the refresh token and clears authentication cookies.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        if(refreshToken != null) {
           refreshTokenService.deleteByToken(refreshToken);
        }
        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .build();

    }
}
