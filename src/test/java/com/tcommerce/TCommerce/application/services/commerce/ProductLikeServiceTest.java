package com.tcommerce.TCommerce.application.services.commerce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductLikeEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth.JpaUserRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductLikeRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductRepository;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductLikeResponse;

@ExtendWith(MockitoExtension.class)
class ProductLikeServiceTest {

    @Mock
    private JpaProductLikeRepository likeRepository;
    @Mock
    private JpaProductRepository productRepository;
    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private ProductLikeService productLikeService;

    private String productId = "prod-id";
    private String userId = "user-id";
    private ProductEntity testProduct;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testProduct = new ProductEntity();
        testProduct.setId(productId);
        
        testUser = new UserEntity();
        testUser.setId(userId);
    }

    @Test
    void likeProduct_ShouldSaveLike_WhenNotAlreadyLiked() {
        when(likeRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(likeRepository.countByProductId(productId)).thenReturn(1L);

        ProductLikeResponse response = productLikeService.likeProduct(productId, userId);

        assertThat(response.likedByMe()).isTrue();
        assertThat(response.totalLikes()).isEqualTo(1L);
        verify(likeRepository).save(any(ProductLikeEntity.class));
    }

    @Test
    void likeProduct_ShouldNotSaveLike_WhenAlreadyLiked() {
        when(likeRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);
        when(likeRepository.countByProductId(productId)).thenReturn(1L);

        ProductLikeResponse response = productLikeService.likeProduct(productId, userId);

        assertThat(response.likedByMe()).isTrue();
        verify(likeRepository, never()).save(any(ProductLikeEntity.class));
    }

    @Test
    void unlikeProduct_ShouldDeleteLike() {
        when(productRepository.existsById(productId)).thenReturn(true);
        when(likeRepository.countByProductId(productId)).thenReturn(0L);

        ProductLikeResponse response = productLikeService.unlikeProduct(productId, userId);

        assertThat(response.likedByMe()).isFalse();
        assertThat(response.totalLikes()).isEqualTo(0L);
        verify(likeRepository).deleteByProductIdAndUserId(productId, userId);
    }
}
