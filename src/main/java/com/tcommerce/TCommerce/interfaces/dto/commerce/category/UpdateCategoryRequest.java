package com.tcommerce.TCommerce.interfaces.dto.commerce.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name
) {}