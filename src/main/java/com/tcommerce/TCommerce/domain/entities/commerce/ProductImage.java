package com.tcommerce.TCommerce.domain.entities.commerce;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;  

public class ProductImage implements BaseEntity {
    private String id;
    private String imageUrl;
    private int displayOrder;
    private String productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductImage(String id, String imageUrl, int displayOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductImage(String productId, String imageUrl, int displayOrder) {
        this.id = String.valueOf(UUID.randomUUID());
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); 

    }

    public ProductImage(String id, String productId, String imageUrl, int displayOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt; 

    }

    public ProductImage(String imageUrl, int displayOrder, String productId) {
        this.id = String.valueOf(UUID.randomUUID());
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.productId = productId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }   

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
