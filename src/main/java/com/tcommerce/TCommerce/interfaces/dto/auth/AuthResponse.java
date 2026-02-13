package com.tcommerce.TCommerce.interfaces.dto.auth;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String email;
    private ERole role;

    public AuthResponse(String token, String id, String email, ERole role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
