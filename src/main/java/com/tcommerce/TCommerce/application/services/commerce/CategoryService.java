package com.tcommerce.TCommerce.application.services.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.exceptions.AlreadyExistsException;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CreateCategoryRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.UpdateCategoryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    @Transactional
    public Category createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new AlreadyExistsException("Category already exists with name: " + request.name(), "CATEGORY_ALREADY_EXISTS");
        }
        Category category = new Category(
                request.name(),
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(String id, UpdateCategoryRequest request) {
        Category category = getCategoryById(id);
        if (request.name() != null && !request.name().equals(category.getName())) {
            if (categoryRepository.existsByName(request.name())) {
                throw new AlreadyExistsException("Category already exists with name: " + request.name(), "CATEGORY_ALREADY_EXISTS");
            }
            category.setName(request.name());
        }
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String id) {
        if (!categoryRepository.findById(id).isPresent()) {
             throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}