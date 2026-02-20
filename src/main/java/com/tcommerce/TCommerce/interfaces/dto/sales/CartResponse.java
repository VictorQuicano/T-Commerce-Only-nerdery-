package com.tcommerce.TCommerce.interfaces.dto.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
    String id,
    String userId,
    List<CartItemResponse> items,
    BigDecimal totalPrice,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
