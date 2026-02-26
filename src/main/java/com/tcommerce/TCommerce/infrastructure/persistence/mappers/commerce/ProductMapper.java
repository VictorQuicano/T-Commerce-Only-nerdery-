package com.tcommerce.TCommerce.infrastructure.persistence.mappers.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.StockEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final ProductImageMapper productImageMapper;

    public ProductMapper(CategoryMapper categoryMapper, ProductImageMapper productImageMapper) {
        this.categoryMapper = categoryMapper;
        this.productImageMapper = productImageMapper;
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .isActive(entity.isActive())
                .category(categoryMapper.toDomain(entity.getCategory()))
                .stock(toDomain(entity.getStock()))
                .images(productImageMapper.toDomainList(entity.getImages()))
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;

        ProductEntity entity = ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .isActive(domain.isActive())
                .build();

        if (domain.getCategory() != null) {
            entity.setCategory(categoryMapper.toEntity(domain.getCategory()));
        }

        if (domain.getStock() != null) {
            StockEntity stockEntity = toEntity(domain.getStock());
            stockEntity.setProduct(entity);
            entity.setStock(stockEntity);
        }

        List<ProductImageEntity> imageEntities = productImageMapper.toEntityList(domain.getImages());
        imageEntities.forEach(imageEntity -> imageEntity.setProduct(entity));
        entity.setImages(imageEntities);

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
                domain.getQuantity(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
