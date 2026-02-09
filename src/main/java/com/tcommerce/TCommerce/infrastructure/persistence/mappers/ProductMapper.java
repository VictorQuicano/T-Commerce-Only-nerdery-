package com.tcommerce.TCommerce.infrastructure.persistence.mappers;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.CategoryEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.StockEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
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

        if (domain.getCategoryId() != null) {
            // Used for reference only, repositories should handle full entity loading if needed.
            // Using a partial entity for ID mapping.
            // WARNING: This assumes that name is not needed for just setting the ID on persistence if using EntityManager.getReference
            // However, creating a new Entity with just ID might fail validation if Name is nullable=false and we try to persist it as new.
            // But here we are mapping Product, and we will set Category on Product.
            // If JPA finds an entity with this ID, it manages the relationship.
            CategoryEntity category = new CategoryEntity(domain.getCategoryId(), "", null, null);
            // We set name to "" to avoid null pointer if accessed, but validation might fail if we tried to save THIS category.
            // Ideally we fetch the category reference.
            // For now, we set it. The Repository should ideally resolve this.
            // But since this is a mapper, we do what we can.
            // A better way is to leave it null here and let the Service/Repository resolve it, OR
            // Expect the repository to merge/attach.
            entity.setCategory(category);
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
