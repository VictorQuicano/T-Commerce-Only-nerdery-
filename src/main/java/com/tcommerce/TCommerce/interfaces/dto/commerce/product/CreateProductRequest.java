package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import com.tcommerce.TCommerce.interfaces.validation.annotations.ValidImageList;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
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
        @Min(value = 1, message = "Price must be greater than 0")
        BigInteger price,

        @NotBlank(message = "Category ID is mandatory")
        String categoryId,

        @NotNull(message = "Stock is mandatory")
        @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
        BigInteger stockQuantity
) {}
