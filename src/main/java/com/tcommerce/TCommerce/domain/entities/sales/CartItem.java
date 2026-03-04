package com.tcommerce.TCommerce.domain.entities.sales;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.interfaces.dto.sales.CartItemResponse;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements BaseEntity {
    private String id;
    private String cartId;
    private Product product;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public CartItemResponse toResponse() {
        return new CartItemResponse(
                this.id,
                this.product.getId(),
                this.product.getName(),
                this.quantity,
                this.product.getPrice(),
                this.product.getPrice().multiply(BigInteger.valueOf(this.quantity)),
                this.createdAt,
                this.updatedAt
        );
    }
}
