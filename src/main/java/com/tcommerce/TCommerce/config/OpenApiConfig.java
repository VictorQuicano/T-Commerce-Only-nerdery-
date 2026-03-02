package com.tcommerce.TCommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "TCommerce API",
        version = "1.0",
        description = "This is the API documentation for TCommerce, a modern e-commerce platform built with Spring Boot. " +
                      "It provides comprehensive features for user authentication, product management, " +
                      "cart operations, order processing, and Stripe payment integration.",
        contact = @Contact(
            name = "TCommerce Team",
            email = "support@tcommerce.com"
        )
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT Authorization header using the Bearer scheme."
)
public class OpenApiConfig {
}
