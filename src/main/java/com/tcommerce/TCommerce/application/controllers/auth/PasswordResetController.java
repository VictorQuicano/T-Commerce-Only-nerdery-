package com.tcommerce.TCommerce.application.controllers.auth;

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
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> requestReset(@Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetResponse response = passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token,@Valid @RequestBody ChangePasswordRequest request) {
        passwordResetService.resetPassword(token, request);
        return ResponseEntity.ok("Password reset successfully");
    }
}
