package com.tcommerce.TCommerce.application.services.auth;


import com.tcommerce.TCommerce.interfaces.dto.auth.ChangePasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.ResetPasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.PasswordResetResponse;

public interface PasswordResetService {
    PasswordResetResponse requestPasswordReset(ResetPasswordRequest request);
    void resetPassword(String token, ChangePasswordRequest request);
}
