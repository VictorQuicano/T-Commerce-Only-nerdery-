package com.tcommerce.TCommerce.application.controllers.sales;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderWithHistory;
import com.tcommerce.TCommerce.domain.entities.sales.Order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPaths.V1 + "/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(orderService.createOrderFromCart(userDetails.getId()).toResponse());
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<OrderResponse> orders = orderService.getUserOrders(userDetails.getId()).stream()
                .map(order -> order.toResponse())
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String orderId) {

        OrderResponse order = orderService.getOrderById(orderId).toResponse();
        
        if (!order.userId().equals(userDetails.getId())) {
             return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(order);
    }
    @GetMapping("/{orderId}/history")
    public ResponseEntity<OrderWithHistory> getOrderHistory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable String orderId) {

        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order.toResponseWithHistory());
    }
}
