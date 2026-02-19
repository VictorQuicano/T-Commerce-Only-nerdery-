package com.tcommerce.TCommerce.domain.entities.commerce;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductImageResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;


@Getter
@Setter
public class ProductImage implements BaseEntity {
    private String id;
    private String imageUrl;
    private int displayOrder;
    private String productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductImage() {
    }

    public ProductImage(String productId, String imageUrl, int displayOrder) {
        this.id = UUID.randomUUID().toString();
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ProductImage(String id, String imageUrl, int displayOrder, String productId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.productId = productId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductImageResponse toResponse() {
        return new ProductImageResponse(
                this.id,
                this.imageUrl,
                this.displayOrder,
                this.createdAt,
                this.updatedAt
        );
    }
}
