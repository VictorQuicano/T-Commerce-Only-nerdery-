package com.tcommerce.TCommerce.infrastructure.persistence.mappers.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;

        return new Category(
                entity.getName(),
                entity.getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;

        return new CategoryEntity(
                domain.getId(),
                domain.getName(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}