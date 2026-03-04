package com.tcommerce.TCommerce.domain.entities.commerce;


import com.tcommerce.TCommerce.domain.entities.BaseEntity;

import java.time.LocalDateTime;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryShortResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category implements BaseEntity {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoryResponse toResponse() {
        return new CategoryResponse(
                this.id,
                this.name,
                this.createdAt,
                this.updatedAt
        );
    }
    public CategoryShortResponse toShortResponse() {
        return new CategoryShortResponse(
                this.id,
                this.name
        );
    }
}
