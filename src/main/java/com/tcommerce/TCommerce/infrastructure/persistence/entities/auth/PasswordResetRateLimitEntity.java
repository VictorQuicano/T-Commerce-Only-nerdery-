package com.tcommerce.TCommerce.infrastructure.persistence.entities.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;


@Entity
@Table(
    name = "password_reset_rate_limits",
    uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PasswordResetRateLimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "attempts", nullable = false )
    private int attempts;

    @Column(name = "blocked_until", nullable = false)
    private LocalDateTime blockedUntil;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
