package com.tcommerce.TCommerce.application.controllers.manager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductFullResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductPriceHistoryResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;
import com.tcommerce.TCommerce.interfaces.validation.annotations.ValidImageList;

import jakarta.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.tcommerce.TCommerce.application.query.ProductPaginationRequest;
import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.application.services.common.PageProcessor;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductListResponse;
import org.springframework.data.domain.Window;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping(ApiPaths.V1 + "/manager/products")
@PreAuthorize("hasAnyRole('MANAGER')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Administrative endpoints for managing products, categories, and orders")
@SecurityRequirement(name = "bearerAuth")
public class ManagerProductController extends PageProcessor {

    private final ProductService productService;

    @Operation(
        summary = "Get all products (Admin View)",
        description = "Administrative endpoint to list all products with extended filters like visibility and deletion status.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
        }
    )
    @GetMapping
    public ResponseEntity<Window<ProductListResponse>> getAllProducts(
            @Parameter(description = "Pagination and filter parameters") ProductPaginationRequest request) {
            
        ProductFilter filter = new ProductFilter(
            request.name(), 
            request.categoryId(), 
            request.isActive(), 
            request.isDeleted(),
            null
        );
        
        PaginationCriteria criteria = processRequest(request);

        ScrollPosition position = ScrollPosition.keyset();

        Sort sort = Sort.unsorted();
        if (request.sortBy() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.sortOrder()) 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, request.sortBy());
        }

        if (sort.isUnsorted()) {
             sort = Sort.by("id").descending();
        }
        
        int limit = criteria.limit();
        
        Window<Product> result = productService.getAllProducts(filter, position, limit, sort);
        
        Window<ProductListResponse> response = result.map(Product::toResponse);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Create a new product",
        description = "Administrative endpoint to create a new product.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                         content = @Content(schema = @Schema(implementation = ProductFullResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PostMapping
    public ResponseEntity<ProductFullResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/products/" + product.getId()))
                .body(product.toFullResponse());
    }

    @Operation(
        summary = "Update a product",
        description = "Administrative endpoint to update product details. Supports partial updates.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                         content = @Content(schema = @Schema(implementation = ProductFullResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
        }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ProductFullResponse> updateProduct(
            @Parameter(description = "The unique identifier of the product") @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product.toFullResponse());
    }
    
    @Operation(
        summary = "Add images to product",
        description = "Upload and associate images with an existing product.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Images uploaded successfully",
                         content = @Content(schema = @Schema(implementation = ProductFullResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
        }
    )
    @PostMapping(value = "/{id}/images", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductFullResponse> addImages(
            @Parameter(description = "The unique identifier of the product") @PathVariable String id,
            @Parameter(description = "The image files to upload") 
            @RequestParam("images") 
            @ValidImageList
            List<MultipartFile> images) {
        Product product = productService.addProductImages(id, images);
        return ResponseEntity.ok(product.toFullResponse());
    }

    @Operation(
        summary = "Remove image from product",
        description = "Deletes an image association from a product.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Image removed successfully"),
            @ApiResponse(responseCode = "404", description = "Product or image not found")
        }
    )
    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @Parameter(description = "The unique identifier of the product") @PathVariable String id,
            @Parameter(description = "The unique identifier of the image to remove") @PathVariable String imageId) {
        productService.removeProductImage(id, imageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Soft delete product",
        description = "Administrative endpoint to mark a product as deleted.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "The unique identifier of the product to delete") @PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get product price history",
        description = "Returns a history of all price changes for a specific product.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Price history retrieved successfully")
        }
    )
    @GetMapping("/{id}/price-history")
    public ResponseEntity<List<ProductPriceHistoryResponse>> getProductPriceHistory(
            @Parameter(description = "The unique identifier of the product") @PathVariable String id) {
        List<com.tcommerce.TCommerce.domain.entities.commerce.ProductPriceHistory> history = productService.getPriceHistory(id);
        List<ProductPriceHistoryResponse> response = history.stream()
                .map(h -> new ProductPriceHistoryResponse(h.getId(), h.getPrice(), h.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
