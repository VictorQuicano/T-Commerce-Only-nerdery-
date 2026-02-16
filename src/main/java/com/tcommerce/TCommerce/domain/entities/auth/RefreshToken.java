package com.tcommerce.TCommerce.domain.entities.auth;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken {
    private String id;
    private String token;
    private User user;
    private LocalDateTime createdAt;
    private Instant expiresAt;
    private LocalDateTime revokedAt;
}
