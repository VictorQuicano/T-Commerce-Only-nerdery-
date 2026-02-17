package com.tcommerce.TCommerce.application.controllers.commerce;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.query.ProductPaginationRequest;
import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductFullResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductImageResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductStockResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryShortResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductListResponse;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.common.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPaths.V1 + "/products")
@CrossOrigin(origins = "*")
public class ProductController{

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductListResponse>> getAllProducts(ProductPaginationRequest request) {
        PaginatedResult<Product> result = productService.getAllProducts(request);
        List<ProductListResponse> responses = result.data().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PaginatedResponse<>(responses, result.pageInfo()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductFullResponse> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(mapToFullResponse(product));
    }

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductFullResponse> createProduct(@Valid @ModelAttribute CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/products/" + product.getId()))
                .body(mapToFullResponse(product));
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductFullResponse> updateProduct(
            @PathVariable String id,
            @Valid @ModelAttribute UpdateProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be empty");
        }
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(mapToFullResponse(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ProductFullResponse mapToFullResponse(Product product) {
        List<ProductImageResponse> imageResponses = product.getImages() != null
        ? product.getImages().stream()
            .map(img -> new ProductImageResponse(
                    img.getId(),
                    img.getImageUrl(),
                    img.getDisplayOrder(),
                    img.getCreatedAt(),
                    img.getUpdatedAt()
            ))
            .collect(Collectors.toList())
        : List.of();

        CategoryResponse categoryResponse = product.getCategory() != null ? new CategoryResponse(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getCreatedAt(),
                product.getCategory().getUpdatedAt()
        ) : null;

        ProductStockResponse stockResponse = product.getStock() != null ? new ProductStockResponse(
                product.getStock().getQuantity(),
                product.getStock().getUpdatedAt()
        ) : null;

        return new ProductFullResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                categoryResponse,
                stockResponse,
                imageResponses,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private ProductListResponse mapToResponse(Product product) {
        List<String> imageUrls = product.getImages() != null
                ? product.getImages().stream().map(img -> img.getImageUrl()).collect(Collectors.toList())
                : List.of();
        CategoryShortResponse categoryResponse = product.getCategory() != null ? new CategoryShortResponse(
                product.getCategory().getId(),
                product.getCategory().getName()
        ) : null;

        BigInteger stockQuantity = product.getStock() != null ? product.getStock().getQuantity() : BigInteger.ZERO;

        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                categoryResponse,
                stockQuantity,
                imageUrls,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
