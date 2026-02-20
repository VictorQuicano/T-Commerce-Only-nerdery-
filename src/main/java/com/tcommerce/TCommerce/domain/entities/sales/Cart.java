package com.tcommerce.TCommerce.domain.entities.sales;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.sales.CartItemResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.CartResponse;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart implements BaseEntity {
    private String id;
    private String userId;
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartResponse toResponse() {
        List<CartItemResponse> itemResponses = this.items.stream()
                .map(item -> item.toResponse())
                .toList();

        BigInteger totalPrice = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigInteger.ZERO, BigInteger::add);

        return new CartResponse(
                this.id,
                this.userId,
                itemResponses,
                totalPrice,
                this.createdAt,
                this.updatedAt
        );
    }
}
