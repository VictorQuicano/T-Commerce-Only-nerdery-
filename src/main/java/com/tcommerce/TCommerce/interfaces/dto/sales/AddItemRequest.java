package com.tcommerce.TCommerce.interfaces.dto.sales;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
    @NotBlank String productId,
    @NotNull @Min(1) Integer quantity
) {}
