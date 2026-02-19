package com.tcommerce.TCommerce.interfaces.dto.auth;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
