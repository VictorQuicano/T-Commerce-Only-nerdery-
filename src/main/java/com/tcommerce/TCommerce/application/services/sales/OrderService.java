package com.tcommerce.TCommerce.application.services.sales;

import com.tcommerce.TCommerce.domain.entities.sales.*;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public Order createOrderFromCart(String userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from an empty cart");
        }

        LocalDateTime now = LocalDateTime.now();
        String orderId = UUID.randomUUID().toString();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .items(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .orderId(orderId)
                        .productId(cartItem.getProduct().getId())
                        .productName(cartItem.getProduct().getName())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);

        OrderStatusHistory initialHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .fromStatus(null)
                .toStatus(OrderStatus.PENDING)
                .changedAt(now)
                .changedBy(userId)
                .reason("Order created")
                .createdAt(now)
                .build();
        order.getStatusHistory().add(initialHistory);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);
        return savedOrder;
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public Order updateOrderStatus(String orderId, OrderStatus nextStatus, String userId, String reason) {
        Order order = getOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();

        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .fromStatus(currentStatus)
                .toStatus(nextStatus)
                .changedAt(LocalDateTime.now())
                .changedBy(userId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        order.setStatus(nextStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);

        return orderRepository.save(order);
    }
}
