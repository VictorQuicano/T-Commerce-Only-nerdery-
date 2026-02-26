package com.tcommerce.TCommerce.interfaces.dto.sales;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatus(
    @NotNull
    OrderStatus status,
    @Size(min=10)
    String reason
) {}
