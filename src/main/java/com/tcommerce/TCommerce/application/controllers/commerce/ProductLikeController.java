package com.tcommerce.TCommerce.application.controllers.commerce;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.commerce.ProductLikeService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.V1 + "/products")
@RequiredArgsConstructor
public class ProductLikeController {

    private final ProductLikeService likeService;

    @PostMapping("/{productId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductLikeResponse> likeProduct(
            @PathVariable String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProductLikeResponse response = likeService.likeProduct(productId, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductLikeResponse> unlikeProduct(
            @PathVariable String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProductLikeResponse response = likeService.unlikeProduct(productId, userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
