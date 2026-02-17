package com.tcommerce.TCommerce.application.controllers.commerce;

import com.tcommerce.TCommerce.application.services.commerce.CategoryService;
import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CreateCategoryRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.UpdateCategoryRequest;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.V1 + "/categories")
@CrossOrigin(origins = "*") 
public class CategoryController{

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> response = categories.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(mapToResponse(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> searchCategories(
            @RequestParam(required = false) String name) {

        List<Category> categories;
        if (name != null && !name.trim().isEmpty()) {
            categories = categoryService.getCategoriesByName(name);
        } else {
            categories = categoryService.getAllCategories();
        }

        List<CategoryResponse> response = categories.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return ResponseEntity.created(URI.create("/categories/" + category.getId()))
                .body(mapToResponse(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        Category category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(mapToResponse(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
