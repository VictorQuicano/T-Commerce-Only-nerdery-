package com.tcommerce.TCommerce.application.controllers.commerce;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import com.tcommerce.TCommerce.application.services.commerce.CategoryService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Commerce", description = "Public endpoints for browsing products and categories")
public class CategoryController{

    private final CategoryService categoryService;

    @Operation(
        summary = "Get all categories",
        description = "Returns a list of all available product categories.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
        }
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> response = categories.stream()
                .map(Category::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get category by ID",
        description = "Returns detailed information about a specific category.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Category found", 
                         content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "The unique identifier of the category") @PathVariable String id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category.toResponse());
    }

}
