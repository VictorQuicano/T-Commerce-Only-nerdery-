package com.tcommerce.TCommerce.application.controllers.sales;

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
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userDetails.getId()).toResponse());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userDetails.getId(), request.productId(), request.quantity()).toResponse());
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userDetails.getId(), productId).toResponse());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
