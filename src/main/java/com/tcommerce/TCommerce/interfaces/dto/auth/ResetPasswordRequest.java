package com.tcommerce.TCommerce.interfaces.dto.auth;

import com.tcommerce.TCommerce.interfaces.validation.annotations.ExistEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ResetPasswordRequest(@NotBlank(message = "Email is required")
    @Size(max = 100)
    @Email @ExistEmail String email) {
}    
