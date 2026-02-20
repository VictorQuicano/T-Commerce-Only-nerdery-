package com.tcommerce.TCommerce.interfaces.dto.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
    String id,
    String productId,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
