package com.tcommerce.TCommerce.application.controllers.commerce;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.query.ProductPaginationRequest;
import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.application.services.common.PageProcessor;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductFullResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductListResponse;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.V1 + "/products")
@RequiredArgsConstructor
@Tag(name = "Commerce", description = "Public endpoints for browsing products and categories")
public class ProductController extends PageProcessor {

    private final ProductService productService;

    @Operation(
        summary = "Get all products with filters",
        description = "Returns a paginated list of products based on filters like name and category.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
        }
    )
    @GetMapping
    public ResponseEntity<Window<ProductListResponse>> getAllProducts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "Pagination and filter parameters") ProductPaginationRequest request) {
            
        String likedByUserId = (request.onlyLiked() != null && request.onlyLiked() && userDetails != null) 
                ? userDetails.getId() 
                : null;

        ProductFilter filter = new ProductFilter(request.name(), request.categoryId(), true, false, likedByUserId);
        
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
        summary = "Get product by ID",
        description = "Returns full details of a specific product.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Product found", 
                         content = @Content(schema = @Schema(implementation = ProductFullResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductFullResponse> getProductById(
            @Parameter(description = "The unique identifier of the product") @PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product.toFullResponse());
    }

}
