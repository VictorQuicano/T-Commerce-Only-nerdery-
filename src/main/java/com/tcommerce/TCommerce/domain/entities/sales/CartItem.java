package com.tcommerce.TCommerce.domain.entities.sales;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.sales.CartItemResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements BaseEntity {
    private String id;
    private String cartId;
    private String itemId; // Referring to Product ID
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public CartItemResponse toResponse() {
        return new CartItemResponse(
                this.id,
                this.itemId,
                this.quantity,
                this.price,
                this.price.multiply(BigDecimal.valueOf(this.quantity)),
                this.createdAt,
                this.updatedAt
        );
    }
}
