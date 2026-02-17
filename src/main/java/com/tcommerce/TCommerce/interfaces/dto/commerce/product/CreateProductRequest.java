package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public record CreateProductRequest(
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        @NotBlank(message = "Description is mandatory")
        @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
        String description,

        @NotNull(message = "Price is mandatory")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Category ID is mandatory")
        String categoryId,

        @NotNull(message = "Stock is mandatory")
        @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
        BigInteger stockQuantity,

        @NotNull(message = "Images are mandatory")
        @NotEmpty(message = "Images list cannot be empty")
        List<MultipartFile> images
) {}
