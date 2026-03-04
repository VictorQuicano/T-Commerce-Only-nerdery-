package com.tcommerce.TCommerce.graphql.resolver;

import com.tcommerce.TCommerce.application.services.commerce.ProductLikeService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LikeMutationResolver {

    private final ProductLikeService productLikeService;

    @MutationMapping
    @PreAuthorize("hasRole('CLIENT')")
    public boolean toggleLike(
            @Argument String productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isCurrentlyLiked = productLikeService.isLikedByUser(productId, userDetails.getId());
        if (isCurrentlyLiked) {
            productLikeService.unlikeProduct(productId, userDetails.getId());
            return false;
        } else {
            productLikeService.likeProduct(productId, userDetails.getId());
            return true;
        }
    }
}
