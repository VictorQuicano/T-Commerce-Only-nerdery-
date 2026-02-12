package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.time.LocalDateTime;

public record ProductImageResponse(
    String id,
    String imageUrl,
    int displayOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
