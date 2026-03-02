package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigInteger;

public record UpdateProductRequest(
        @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        String description,

        @Min(value = 0, message = "Price must be greater than or equal to 0")
        BigInteger price,

        Boolean isActive,

        String categoryId,

        @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
        BigInteger stockQuantity
) {}
