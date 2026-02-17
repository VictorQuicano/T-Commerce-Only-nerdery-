package com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;


@Entity
@Table(name="stock_levels")
public class StockEntity extends BaseEntity {
    @Column
    private BigInteger quantity;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private ProductEntity product;

    public StockEntity(){}

    public StockEntity(String id, BigInteger quantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.quantity = quantity;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
