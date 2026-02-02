package com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="stock_levels")
public class StockEntity extends BaseEntity {
    @Column
    private int quantity;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private ProductEntity product;

    public StockEntity(){}

    public StockEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, int quantity) {
        super(id, createdAt, updatedAt);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
