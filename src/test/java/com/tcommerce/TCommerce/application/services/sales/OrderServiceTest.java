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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.sales.Cart;
import com.tcommerce.TCommerce.domain.entities.sales.CartItem;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderItem;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartService cartService;
    @Mock
    private ChangeStatusNotificationService changeStatusNotificationService;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;

    private OrderService orderService;

    private String userId = "user-id";
    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository, cartService, changeStatusNotificationService, productService, paymentService);

        testOrder = Order.builder()
                .id("order-id")
                .userId(userId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .build();
    }

    @Test
    void createOrderFromCart_ShouldCreateOrderAndClearCart() {
        
        Product product = Product.builder().id("p1").name("Prod 1").price(BigInteger.TEN).build();
        CartItem item = CartItem.builder().product(product).quantity(2).build();
        Cart cart = Cart.builder().items(List.of(item)).build();

        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        
        Order response = orderService.createOrderFromCart(userId);

        
        assertThat(response).isNotNull();
        verify(cartService).clearCart(userId);
        verify(orderRepository).save(any(Order.class));
        verify(changeStatusNotificationService).notifyStatusChange(anyString(), any(), any(), anyString(), anyString());
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatusAndNotify() {
        
        when(orderRepository.findById("order-id")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        
        orderService.updateOrderStatus("order-id", OrderStatus.PAID, "admin-id", "Paid successfully");

        
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(changeStatusNotificationService).notifyStatusChange(org.mockito.ArgumentMatchers.eq("order-id"), any(), org.mockito.ArgumentMatchers.eq(OrderStatus.PAID), anyString(), anyString());
    }

    @Test
    void cancelOrder_ShouldRestockAndProcessRefund_WhenPaid() {
        
        testOrder.setStatus(OrderStatus.PAID);
        OrderItem orderItem = OrderItem.builder().productId("p1").quantity(2).price(BigInteger.TEN).build();
        testOrder.setItems(List.of(orderItem));
        Product product = Product.builder().id("p1").build();

        when(productService.getProductById("p1")).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        
        orderService.cancelOrder(testOrder, userId, "Cancelled by user");

        
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(productService).increaseStock(org.mockito.ArgumentMatchers.eq(product), org.mockito.ArgumentMatchers.eq(BigInteger.valueOf(2)));
        verify(paymentService).refundOrder(org.mockito.ArgumentMatchers.eq(testOrder), any(BigInteger.class), anyString());
    }
}
