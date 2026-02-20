package com.tcommerce.TCommerce.interfaces.dto.sales;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    String id,
    String userId,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
