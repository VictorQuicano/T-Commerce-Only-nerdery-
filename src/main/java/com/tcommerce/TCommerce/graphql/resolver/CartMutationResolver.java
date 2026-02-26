package com.tcommerce.TCommerce.graphql.resolver;

import com.tcommerce.TCommerce.application.services.sales.CartService;
import com.tcommerce.TCommerce.domain.entities.sales.Cart;
import com.tcommerce.TCommerce.graphql.mapper.GraphQLMapper;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartMutationResolver {

    private final CartService cartService;

    @MutationMapping
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Object> addToCart(
            @Argument String productId,
            @Argument int quantity,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Cart cart = cartService.addItemToCart(userDetails.getId(), productId, quantity);
        return GraphQLMapper.toGraphQLCart(cart);
    }

    @MutationMapping
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Object> removeFromCart(
            @Argument String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Cart cart = cartService.removeItemFromCart(userDetails.getId(), productId);
        return GraphQLMapper.toGraphQLCart(cart);
    }

    @MutationMapping
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Object> clearCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Cart cart = cartService.clearCart(userDetails.getId());
        return GraphQLMapper.toGraphQLCart(cart);
    }
}
