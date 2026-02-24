package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Cart;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.CartRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.CartEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.CartItemEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final JpaCartRepository jpaCartRepository;
    private final CartMapper cartMapper;

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return jpaCartRepository.findByUser_Id(userId)
                .map(cartMapper::toDomain);
    }

    @Override
    @Transactional
    public Cart save(Cart cart) {
        // Use fetch-then-update pattern to handle managed entities and orphan removal correctly
        if (cart.getId() != null) {
            Optional<CartEntity> existing = jpaCartRepository.findById(cart.getId());
            if (existing.isPresent()) {
                CartEntity entity = existing.get();
                entity.setUpdatedAt(cart.getUpdatedAt());

                // Clear and rebuild items for orphan removal
                entity.getItems().clear();
                List<CartItemEntity> itemEntities = cart.getItems().stream()
                        .map(item -> cartMapper.toEntity(item, entity))
                        .collect(Collectors.toList());
                entity.getItems().addAll(itemEntities);

                CartEntity savedEntity = jpaCartRepository.save(entity);
                return cartMapper.toDomain(savedEntity);
            }
        }

        CartEntity entity = cartMapper.toEntity(cart);
        CartEntity savedEntity = jpaCartRepository.save(entity);
        return cartMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        jpaCartRepository.deleteByUser_Id(userId);
    }
}

