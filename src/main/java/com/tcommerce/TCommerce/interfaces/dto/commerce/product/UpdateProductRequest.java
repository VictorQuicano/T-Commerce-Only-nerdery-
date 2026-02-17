package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public record UpdateProductRequest(
        @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        String description,

        @Min(value = 0, message = "Price must be greater than or equal to 0")
        BigDecimal price,

        String categoryId,

        @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
        BigInteger stockQuantity,

        List<MultipartFile> images
) {}
