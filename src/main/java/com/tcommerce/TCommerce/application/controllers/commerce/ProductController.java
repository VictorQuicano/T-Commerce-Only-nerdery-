package com.tcommerce.TCommerce.application.controllers.commerce;

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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.V1 + "/products")
@RequiredArgsConstructor
public class ProductController extends PageProcessor {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Window<ProductListResponse>> getAllProducts(
            ProductPaginationRequest request) {
            
        ProductFilter filter = new ProductFilter(request.name(), request.categoryId());
        
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductFullResponse> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product.toFullResponse());
    }

}
