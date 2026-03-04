package com.tcommerce.TCommerce.interfaces.dto.auth;

import lombok.Builder;

@Builder
public record PasswordResetResponse(
    String message,
    Integer attemptsRemaining,
    Long waitTimeSeconds
) {}
