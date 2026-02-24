package com.tcommerce.TCommerce.interfaces.dto.sales;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
    @NotBlank String orderId
) {}
