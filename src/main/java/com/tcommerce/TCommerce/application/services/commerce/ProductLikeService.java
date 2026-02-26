package com.tcommerce.TCommerce.application.services.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductLikeEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth.JpaUserRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductLikeRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductRepository;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final JpaProductLikeRepository likeRepository;
    private final JpaProductRepository productRepository;
    private final JpaUserRepository userRepository;

    @Transactional
    public ProductLikeResponse likeProduct(String productId, String userId) {
        
        boolean alreadyLiked = likeRepository.existsByProductIdAndUserId(productId, userId);
        
        if (!alreadyLiked) {
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                    
            ProductLikeEntity newLike = ProductLikeEntity.builder()
                    .product(product)
                    .user(user)
                    .build();
            likeRepository.save(newLike);
        }

        long totalLikes = likeRepository.countByProductId(productId);
        return new ProductLikeResponse(productId, totalLikes, true);
    }

    @Transactional
    public ProductLikeResponse unlikeProduct(String productId, String userId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        likeRepository.deleteByProductIdAndUserId(productId, userId);

        long totalLikes = likeRepository.countByProductId(productId);
        return new ProductLikeResponse(productId, totalLikes, false);
    }

    public long getLikeCount(String productId) {
        return likeRepository.countByProductId(productId);
    }

    public boolean isLikedByUser(String productId, String userId) {
        if (userId == null) return false;
        return likeRepository.existsByProductIdAndUserId(productId, userId);
    }
}
