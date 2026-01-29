package com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="product_images")
public class ProductImage extends BaseEntity {
    @Column(name="imageUrl", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "display_order")
    private int displayOrder;

    public ProductImage() {
    }

    public ProductImage(String imageUrl, Product product, int displayOrder) {
        this.imageUrl = imageUrl;
        this.product = product;
        this.displayOrder = displayOrder;
    }

    public ProductImage(String id, LocalDateTime createdAt, LocalDateTime updatedAt, String imageUrl, Product product, int displayOrder) {
        super(id, createdAt, updatedAt);
        this.imageUrl = imageUrl;
        this.product = product;
        this.displayOrder = displayOrder;
    }
}
