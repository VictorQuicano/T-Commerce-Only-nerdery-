package com.tcommerce.TCommerce.application.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.auth.PasswordResetService;
import com.tcommerce.TCommerce.interfaces.dto.auth.ResetPasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.ChangePasswordRequest;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcommerce.TCommerce.interfaces.dto.auth.PasswordResetResponse;

@RestController
@RequestMapping(ApiPaths.V1 + "/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token management")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    
    @Operation(
        summary = "Request password reset",
        description = "Sends a password reset link to the user's email if the account exists.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reset link sent successfully", 
                         content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format"),
            @ApiResponse(responseCode = "429", description = "Too many requests (rate limit exceeded)")
        }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> requestReset(@Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetResponse response = passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Reset password",
        description = "Resets the user's password using a valid reset token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token, or invalid password format")
        }
    )
    @PutMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "The password reset token sent via email") @PathVariable String token,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordResetService.resetPassword(token, request);
        return ResponseEntity.ok("Password reset successfully");
    }
}
