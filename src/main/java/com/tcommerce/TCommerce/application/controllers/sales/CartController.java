package com.tcommerce.TCommerce.application.controllers.sales;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.CartService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.AddItemRequest;
import com.tcommerce.TCommerce.interfaces.dto.sales.CartResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.V1 + "/cart")
@RequiredArgsConstructor
@Tag(name = "Sales & Checkout", description = "Endpoints for managing the shopping cart and placing orders")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @Operation(
        summary = "Get current user's cart",
        description = "Returns the contents of the cart for the authenticated user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                         content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userDetails.getId()).toResponse());
    }

    @Operation(
        summary = "Add item to cart",
        description = "Adds a product to the user's cart or updates quantity if it already exists.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Item added successfully",
                         content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product or quantity")
        }
    )
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userDetails.getId(), request.productId(), request.quantity()).toResponse());
    }

    @Operation(
        summary = "Remove item from cart",
        description = "Removes a specific product from the user's cart.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully",
                         content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not in cart")
        }
    )
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "The unique identifier of the product to remove") @PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userDetails.getId(), productId).toResponse());
    }

    @Operation(
        summary = "Clear cart",
        description = "Removes all items from the user's cart.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully")
        }
    )
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
