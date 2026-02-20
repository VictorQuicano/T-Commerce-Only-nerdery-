package com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Cart;
import com.tcommerce.TCommerce.domain.entities.sales.CartItem;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.CartEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.CartItemEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth.JpaUserRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    private final JpaUserRepository userRepository;
    private final JpaProductRepository productRepository;

    public CartMapper(JpaUserRepository userRepository, JpaProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Cart toDomain(CartEntity entity) {
        if (entity == null) return null;

        return Cart.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .items(entity.getItems().stream().map(this::toDomain).collect(Collectors.toCollection(ArrayList::new)))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CartItem toDomain(CartItemEntity entity) {
        if (entity == null) return null;

        return CartItem.builder()
                .id(entity.getId())
                .cartId(entity.getCart().getId())
                .itemId(entity.getProduct().getId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public CartEntity toEntity(Cart domain) {
        if (domain == null) return null;

        UserEntity user = userRepository.findById(domain.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + domain.getUserId()));

        CartEntity entity = CartEntity.builder()
                .id(domain.getId())
                .user(user)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        List<CartItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> toEntity(item, entity))
                .collect(Collectors.toCollection(ArrayList::new));
        entity.setItems(itemEntities);

        return entity;
    }

    public CartItemEntity toEntity(CartItem domain, CartEntity cartEntity) {
        if (domain == null) return null;

        ProductEntity product = productRepository.findById(domain.getItemId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + domain.getItemId()));

        return new CartItemEntity(
                domain.getId(),
                cartEntity,
                product,
                domain.getQuantity(),
                domain.getPrice(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getDeletedAt()
        );
    }
}
