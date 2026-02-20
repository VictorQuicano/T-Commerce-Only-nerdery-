package com.tcommerce.TCommerce.domain.entities.commerce;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductFullResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductListResponse;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements BaseEntity {
    private String id;
    private String name;
    private String description;
    private BigInteger price;
    private Category category;
    private Stock stock;
    private List<ProductImage> images;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductFullResponse toFullResponse() {
        return new ProductFullResponse(
                this.id,
                this.name,
                this.description,
                this.price,
                this.category.toResponse(),
                this.stock.toResponse(),
                this.images.stream().map(ProductImage::toResponse).toList(),
                this.createdAt,
                this.updatedAt
        );
    }

    public ProductListResponse toResponse(){
        return new ProductListResponse(
                this.id,
                this.name,
                this.description,
                this.price,
                this.category.toShortResponse(),
                this.stock.getQuantity(),
                this.images.stream().map(ProductImage::getImageUrl).toList(),
                this.createdAt,
                this.updatedAt
        );
    }
}
