package com.tcommerce.TCommerce.infrastructure.persistence.mappers.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductImageMapper {

    public ProductImage toDomain(ProductImageEntity entity) {
        if (entity == null) return null;
        return new ProductImage(
                entity.getId(),
                entity.getImageUrl(),
                entity.getDisplayOrder(),
                entity.getProduct() != null ? entity.getProduct().getId() : null,
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
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ProductImageEntity> toEntityList(List<ProductImage> domains) {
        if (domains == null) return new ArrayList<>();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
