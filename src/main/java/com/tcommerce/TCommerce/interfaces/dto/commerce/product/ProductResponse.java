package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        String categoryId,
        int stockQuantity,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
