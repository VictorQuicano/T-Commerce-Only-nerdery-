package com.tcommerce.TCommerce.domain.entities.auth;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private ERole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}