package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryResponse;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        CategoryResponse category,
        int stockQuantity,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
