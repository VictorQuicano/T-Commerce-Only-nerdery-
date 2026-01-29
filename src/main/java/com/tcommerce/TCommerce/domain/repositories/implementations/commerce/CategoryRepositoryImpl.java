package com.tcommerce.TCommerce.domain.repositories.implementations.commerce;


import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.domain.repositories.implementations.CRUDRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class CategoryRepositoryImpl
        extends CRUDRepositoryImpl<Category>
        implements CategoryRepository {

    @Override
    public Optional<Category> findByName(String name) {
        return data.stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Category> findByNameContaining(String name) {
        return data.stream()
                .filter(category -> category.getName().toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return data.stream()
                .anyMatch(category -> category.getName().equalsIgnoreCase(name));
    }

    @Override
    public Category save(Category category) {
        if (category.getCreatedAt() == null) {
            category.setCreatedAt(LocalDateTime.now());
        }
        category.setUpdatedAt(LocalDateTime.now());

        return super.save(category);
    }
}