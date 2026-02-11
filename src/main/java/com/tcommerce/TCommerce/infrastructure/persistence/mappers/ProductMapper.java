package com.tcommerce.TCommerce.infrastructure.persistence.mappers;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.StockEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                categoryMapper.toDomain(entity.getCategory()),
                toDomain(entity.getStock()),
                toDomain(entity.getImages()),
                entity.getDeletedAt() != null ? java.util.Optional.of(entity.getDeletedAt()) : java.util.Optional.empty(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;

        ProductEntity entity = new ProductEntity(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getDeletedAt().orElse(null)
        );

        if (domain.getCategory() != null) {
            entity.setCategory(categoryMapper.toEntity(domain.getCategory()));
        }

        if (domain.getStock() != null) {
            StockEntity stockEntity = toEntity(domain.getStock());
            stockEntity.setProduct(entity);
            entity.setStock(stockEntity);
        }

        if (domain.getImages() != null) {
            List<ProductImageEntity> imageEntities = domain.getImages().stream()
                    .map(img -> {
                        ProductImageEntity imgEntity = toEntity(img);
                        imgEntity.setProduct(entity); // Set parent reference
                        return imgEntity;
                    })
                    .collect(Collectors.toList());
            entity.setImages(imageEntities);
        } else {
            entity.setImages(new ArrayList<>());
        }

        return entity;
    }

    private Stock toDomain(StockEntity entity) {
        if (entity == null) return null;
        return new Stock(
                entity.getId(),
                entity.getQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private StockEntity toEntity(Stock domain) {
        if (domain == null) return null;
        return new StockEntity(
                domain.getId(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getQuantity()
        );
    }

    private List<ProductImage> toDomain(List<ProductImageEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ProductImage toDomain(ProductImageEntity entity) {
        if (entity == null) return null;
        return new ProductImage(
                entity.getId(),
                entity.getImageUrl(),
                entity.getDisplayOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ProductImageEntity toEntity(ProductImage domain) {
        if (domain == null) return null;
        return new ProductImageEntity(
            domain.getId(),
            domain.getCreatedAt(),
            domain.getUpdatedAt(),
            domain.getImageUrl(),
            null, // product set in parent
            domain.getDisplayOrder()
        );
    }
}
