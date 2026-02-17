package com.tcommerce.TCommerce.application.services.auth;


import com.tcommerce.TCommerce.interfaces.dto.auth.ChangePasswordRequest;
import com.tcommerce.TCommerce.interfaces.dto.auth.ResetPasswordRequest;

public interface PasswordResetService {
    void requestPasswordReset(ResetPasswordRequest request);
    void resetPassword(String token, ChangePasswordRequest request);
}
