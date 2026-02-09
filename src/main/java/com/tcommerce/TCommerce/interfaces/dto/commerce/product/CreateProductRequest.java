package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        String description,

        @NotNull(message = "Price is mandatory")
        @Min(value = 0, message = "Price must be greater than or equal to 0")
        BigDecimal price,

        @NotBlank(message = "Category ID is mandatory")
        String categoryId,

        @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
        int stockQuantity,

        List<String> imageUrls
) {}
