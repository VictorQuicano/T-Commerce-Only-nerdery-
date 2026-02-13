package com.tcommerce.TCommerce.infrastructure.persistence.mappers.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProductImageMapper {

    public ProductImage toDomain(ProductImageEntity entity) {
        if (entity == null) return null;
        return new ProductImage(
                entity.getId(),
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getImageUrl(),
                entity.getDisplayOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProductImageEntity toEntity(ProductImage domain) {
        if (domain == null) return null;
        return new ProductImageEntity(
            domain.getId(),
            domain.getCreatedAt(),
            domain.getUpdatedAt(),
            domain.getImageUrl(),
            null, // product needs to be set separately if needed
            domain.getDisplayOrder()
        );
    }
    public List<ProductImage> toDomainList(List<ProductImageEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).toList();
    }

    public List<ProductImageEntity> toEntityList(List<ProductImage> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).toList();
    }
}
