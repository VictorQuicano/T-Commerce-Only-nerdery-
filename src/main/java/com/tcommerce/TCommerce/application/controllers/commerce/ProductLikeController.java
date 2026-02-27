package com.tcommerce.TCommerce.application.controllers.commerce;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Commerce", description = "Public endpoints for browsing products and categories")
public class ProductLikeController {

    private final ProductLikeService likeService;

    @Operation(
        summary = "Like a product",
        description = "Adds a like to a product for the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Product liked successfully",
                         content = @Content(schema = @Schema(implementation = ProductLikeResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
        }
    )
    @PostMapping("/{productId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductLikeResponse> likeProduct(
            @Parameter(description = "The unique identifier of the product") @PathVariable String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProductLikeResponse response = likeService.likeProduct(productId, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Unlike a product",
        description = "Removes a like from a product for the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Product unliked successfully",
                         content = @Content(schema = @Schema(implementation = ProductLikeResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
        }
    )
    @DeleteMapping("/{productId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductLikeResponse> unlikeProduct(
            @Parameter(description = "The unique identifier of the product") @PathVariable String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ProductLikeResponse response = likeService.unlikeProduct(productId, userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
