package com.tcommerce.TCommerce.infrastructure.persistence.implementations;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.CategoryEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.CategoryMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.JpaCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryRepositoryImpl(JpaCategoryRepository jpaCategoryRepository, CategoryMapper categoryMapper) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll().stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findById(String id) {
        return jpaCategoryRepository.findById(id)
                .map(categoryMapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        CategoryEntity savedEntity = jpaCategoryRepository.save(entity);
        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String id) {
        jpaCategoryRepository.deleteById(id);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaCategoryRepository.findByName(name)
                .map(categoryMapper::toDomain);
    }

    @Override
    public List<Category> findByNameContaining(String name) {
        return jpaCategoryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaCategoryRepository.existsByName(name);
    }
}
