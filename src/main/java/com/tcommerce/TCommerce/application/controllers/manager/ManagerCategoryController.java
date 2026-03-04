package com.tcommerce.TCommerce.application.controllers.manager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.commerce.CategoryService;
import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CreateCategoryRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.UpdateCategoryRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping(ApiPaths.V1 + "/manager/categories")
@PreAuthorize("hasAnyRole('MANAGER')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Administrative endpoints for managing products, categories, and orders")
@SecurityRequirement(name = "bearerAuth")
public class ManagerCategoryController {
    private final CategoryService categoryService;

    @Operation(
        summary = "Create a new category",
        description = "Administrative endpoint to create a new product category.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                         content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager access required")
        }
    )
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return ResponseEntity.created(URI.create("/categories/" + category.getId()))
                .body(category.toResponse());
    }

    @Operation(
        summary = "Update an existing category",
        description = "Administrative endpoint to update category details like name or description.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                         content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager access required")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "The unique identifier of the category") @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        Category category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(category.toResponse());
    }

    @Operation(
        summary = "Delete a category",
        description = "Administrative endpoint to remove a category. This action is irreversible.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager access required")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "The unique identifier of the category to delete") @PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
