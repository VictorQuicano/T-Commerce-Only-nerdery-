package com.tcommerce.TCommerce.application.controllers.manager;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductFullResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
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
public class ManagerProductController extends PageProcessor {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Window<ProductListResponse>> getAllProducts(
            ProductPaginationRequest request) {
            
        ProductFilter filter = new ProductFilter(
            request.name(), 
            request.categoryId(), 
            request.isActive(), 
            request.isDeleted()
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
    
    @PostMapping
    public ResponseEntity<ProductFullResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/products/" + product.getId()))
                .body(product.toFullResponse());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductFullResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product.toFullResponse());
    }
    
    @PostMapping(value = "/{id}/images", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductFullResponse> addImages(
            @PathVariable String id,
            @RequestParam("images") List<MultipartFile> images) {
        Product product = productService.addProductImages(id, images);
        return ResponseEntity.ok(product.toFullResponse());
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @PathVariable String id,
            @PathVariable String imageId) {
        productService.removeProductImage(id, imageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
