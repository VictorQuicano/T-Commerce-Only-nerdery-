package com.tcommerce.TCommerce.domain.entities.auth;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRateLimit {
    private String id;
    private User user;
    private int attempts;
    private LocalDateTime blockedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
