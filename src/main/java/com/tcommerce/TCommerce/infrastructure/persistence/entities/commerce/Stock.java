package com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="stock_levels")
public class Stock extends BaseEntity {
    @Column
    private int quantity;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    public Stock(){}

    public Stock(String id, LocalDateTime createdAt, LocalDateTime updatedAt, int quantity) {
        super(id, createdAt, updatedAt);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
