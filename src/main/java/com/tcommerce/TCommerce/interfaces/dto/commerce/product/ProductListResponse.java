package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryShortResponse;

public record ProductListResponse(
        String id,
        String name,
        String description,
        BigInteger price,
        CategoryShortResponse category,
        BigInteger stockQuantity,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
