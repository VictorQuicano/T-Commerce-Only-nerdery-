package com.tcommerce.TCommerce.interfaces.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.tcommerce.TCommerce.interfaces.validation.annotations.StrongPassword;


public record ChangePasswordRequest(
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20)
    @StrongPassword String password) {
    
}
