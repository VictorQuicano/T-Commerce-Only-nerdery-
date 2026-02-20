package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Cart;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.CartRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.CartEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales.CartMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private final JpaCartRepository jpaCartRepository;
    private final CartMapper cartMapper;

    public CartRepositoryImpl(JpaCartRepository jpaCartRepository, CartMapper cartMapper) {
        this.jpaCartRepository = jpaCartRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return jpaCartRepository.findByUser_Id(userId)
                .map(cartMapper::toDomain);
    }

    @Override
    @Transactional
    public Cart save(Cart cart) {
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
