package com.tcommerce.TCommerce.application.services.sales;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.application.services.commerce.StockNotificationService;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderItem;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StockNotificationService stockNotificationService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private ShippingService shippingService;

    private User testUser;
    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id("user-id").build();
        testProduct = Product.builder().id("prod-id").build();
        
        OrderItem item = OrderItem.builder().productId("prod-id").quantity(2).build();
        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        testOrder = Order.builder()
                .id("order-id")
                .userId("user-id")
                .items(items)
                .build();
    }

    @Test
    void reserveStock_ShouldReduceStock_WhenAvailable() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(testUser));
        when(productService.getProductById("prod-id")).thenReturn(testProduct);
        when(productService.hasEnoughStock(eq(testProduct), any(BigInteger.class))).thenReturn(true);
        when(orderService.save(any(Order.class))).thenReturn(testOrder);

        Order result = shippingService.reserveStock(testOrder);

        assertThat(result.getItems()).hasSize(1);
        verify(productService).reduceStock(eq(testProduct), eq(BigInteger.valueOf(2)));
        verify(orderService).save(testOrder);
    }

    @Test
    void reserveStock_ShouldRemoveItemAndNotify_WhenNotEnoughStock() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(testUser));
        when(productService.getProductById("prod-id")).thenReturn(testProduct);
        when(productService.hasEnoughStock(eq(testProduct), any(BigInteger.class))).thenReturn(false);
        when(orderService.save(any(Order.class))).thenReturn(testOrder);

        Order result = shippingService.reserveStock(testOrder);

        assertThat(result.getItems()).isEmpty();
        verify(stockNotificationService).notifyUserNotEnoughStock(testProduct, testUser);
    }

    @Test
    void startShipping_ShouldUpdateStatus() {
        shippingService.startShipping(testOrder);
        verify(orderService).updateOrderStatus(eq("order-id"), eq(OrderStatus.SHIPPED), anyString(), anyString());
    }

    private Product eq(Product product) {
        return org.mockito.ArgumentMatchers.eq(product);
    }

    private BigInteger eq(BigInteger value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }

    private String eq(String value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }

    private OrderStatus eq(OrderStatus status) {
        return org.mockito.ArgumentMatchers.eq(status);
    }
}
