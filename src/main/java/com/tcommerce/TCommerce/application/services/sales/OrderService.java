package com.tcommerce.TCommerce.application.services.sales;

import com.tcommerce.TCommerce.application.query.OrderFilter;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.sales.*;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;
import com.tcommerce.TCommerce.domain.exceptions.CartEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.tcommerce.TCommerce.application.services.commerce.ProductService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ChangeStatusNotificationService changeStatusNotificationService; 
    private final ProductService productService;

    public Window<Order> getAllOrders(OrderFilter filter, ScrollPosition position, int limit, Sort sort) {
        return orderRepository.findAll(position, limit, filter, sort);
    }

    public Order createOrderFromCart(String userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException(
                "Cannot create order from an empty cart"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .items(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .productId(cartItem.getProduct().getId())
                        .productName(cartItem.getProduct().getName())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);
        String reason = "Order created";
        OrderStatusHistory initialHistory = OrderStatusHistory.builder()
                .fromStatus(null)
                .toStatus(OrderStatus.PENDING)
                .changedAt(now)
                .changedBy(userId)
                .reason(reason)
                .createdAt(now)
                .build();
        order.getStatusHistory().add(initialHistory);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);
        changeStatusNotificationService.notifyStatusChange(savedOrder.getId(), null, OrderStatus.PENDING, userId, reason);
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
                .changedBy("SYSTEM".equals(userId) ? null : userId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        order.setStatus(nextStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);

        Order savedOrder = orderRepository.save(order);
        changeStatusNotificationService.notifyStatusChange(savedOrder.getId(), currentStatus, nextStatus, userId, reason);
        return savedOrder;
    }
    
    public Order cancelOrder(Order order, String userId, String reason) {
        
        OrderStatus currentStatus = order.getStatus();

        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(order.getId())
                .fromStatus(currentStatus)
                .toStatus(OrderStatus.CANCELLED)
                .changedAt(LocalDateTime.now())
                .changedBy("SYSTEM".equals(userId) ? null : userId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);
        order = restockOrder(order);

        Order savedOrder = orderRepository.save(order);
        changeStatusNotificationService.notifyStatusChange(savedOrder.getId(), currentStatus, OrderStatus.CANCELLED, userId, reason);
        return savedOrder;
    }

    public Order restockOrder(Order order){
        order.getItems().forEach(item -> {
            try {
                Product product = productService.getProductById(item.getProductId());
                productService.increaseStock(product, BigInteger.valueOf(item.getQuantity()));
            } catch (Exception e) {
                log.error("Failed to return stock for product {} in order {}: {}", 
                    item.getProductId(), order.getId(), e.getMessage());
            }
        });

        return orderRepository.save(order);
    
    }


    public Order initiatePayment(String orderId, String paymentIntentId, String userId) {
        Order order = getOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();

        OrderStatusHistory history = OrderStatusHistory.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderId)
                .fromStatus(currentStatus)
                .toStatus(OrderStatus.AWAITING_PAYMENT)
                .changedAt(LocalDateTime.now())
                .changedBy(userId)
                .reason("Payment initiated")
                .createdAt(LocalDateTime.now())
                .build();

        order.setPaymentIntentId(paymentIntentId);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order.setUpdatedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);

        Order savedOrder = orderRepository.save(order);
        changeStatusNotificationService.notifyStatusChange(savedOrder.getId(), currentStatus, OrderStatus.AWAITING_PAYMENT, userId, "Payment initiated");
        return savedOrder;
    }
    public Order save(Order order) {
        return orderRepository.save(order);
    }

}

