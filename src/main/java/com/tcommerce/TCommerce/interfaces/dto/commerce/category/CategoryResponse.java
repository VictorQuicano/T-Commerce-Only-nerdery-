package com.tcommerce.TCommerce.interfaces.dto.commerce.category;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;


public record CategoryResponse(
        String id,
        String name,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {}

