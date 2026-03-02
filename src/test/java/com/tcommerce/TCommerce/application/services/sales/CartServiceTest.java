package com.tcommerce.TCommerce.application.services.sales;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.entities.sales.Cart;
import com.tcommerce.TCommerce.domain.entities.sales.CartItem;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.CartRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private String userId = "user-id";
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .id("cart-id")
                .userId(userId)
                .items(new ArrayList<>())
                .build();

        testProduct = Product.builder()
                .id("prod-id")
                .name("Test Product")
                .stock(new Stock(UUID.randomUUID().toString(), BigInteger.TEN, null, null))
                .build();
    }

    @Test
    void getOrCreateCart_ShouldReturnExistingCart_WhenFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getOrCreateCart(userId);

        assertThat(result).isEqualTo(testCart);
    }

    @Test
    void addItemToCart_ShouldAddProduct_WhenStockIsAvailable() {
        
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(testCart));
        when(productService.getProductById("prod-id")).thenReturn(testProduct);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        
        Cart result = cartService.addItemToCart(userId, "prod-id", 2);

        
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
        verify(cartRepository).save(testCart);
    }

    @Test
    void addItemToCart_ShouldThrowException_WhenStockIsNotAvailable() {
        
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(testCart));
        when(productService.getProductById("prod-id")).thenReturn(testProduct);

        
        assertThatThrownBy(() -> cartService.addItemToCart(userId, "prod-id", 11))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Not enough stock");
    }

    @Test
    void clearCart_ShouldRemoveAllItems() {
        
        testCart.getItems().add(CartItem.builder().build());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        
        Cart result = cartService.clearCart(userId);

        
        assertThat(result.getItems()).isEmpty();
        verify(cartRepository).save(testCart);
    }
}
