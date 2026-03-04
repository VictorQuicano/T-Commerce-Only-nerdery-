package com.tcommerce.TCommerce.application.controllers.sales;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderWithHistory;
import com.tcommerce.TCommerce.interfaces.dto.sales.CancelOrderRequest;

import jakarta.validation.Valid;

import org.springframework.security.access.AccessDeniedException;

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
@Tag(name = "Sales & Checkout", description = "Endpoints for managing the shopping cart and placing orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "Create order from cart",
        description = "Converts the current user's cart into a pending order.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                         content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cart is empty")
        }
    )
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(orderService.createOrderFromCart(userDetails.getId()).toResponse());
    }

    @Operation(
        summary = "Get user's orders",
        description = "Returns a list of all orders placed by the authenticated user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
        }
    )
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<OrderResponse> orders = orderService.getUserOrders(userDetails.getId()).stream()
                .map(order -> order.toResponse())
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get order by ID",
        description = "Returns detailed information about a specific order.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order found",
                         content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Unauthorized access to this order")
        }
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "The unique identifier of the order") @PathVariable String orderId) {

        OrderResponse order = orderService.getOrderById(orderId).toResponse();
        
        if (!order.userId().equals(userDetails.getId())) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
        
        return ResponseEntity.ok(order);
    }

    @Operation(
        summary = "Cancel order",
        description = "Cancels a pending or paid order and triggers a refund if applicable.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully",
                         content = @Content(schema = @Schema(implementation = OrderWithHistory.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in current status"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Unauthorized access to this order")
        }
    )
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderWithHistory> cancelOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails, 
            @Parameter(description = "The unique identifier of the order to cancel") @PathVariable String orderId, 
            @RequestBody @Valid CancelOrderRequest request) {
        String userId = userDetails.getId();
        Order order = orderService.getOrderById(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to cancel this order");
        }
        order = orderService.cancelOrder(order, userId, request.reason());
        return ResponseEntity.ok(order.toResponseWithHistory());
    }
    @Operation(
        summary = "Get order history",
        description = "Returns the full status transition history for an order.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order history retrieved successfully",
                         content = @Content(schema = @Schema(implementation = OrderWithHistory.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
        }
    )
    @GetMapping("/{orderId}/history")
    public ResponseEntity<OrderWithHistory> getOrderHistory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(description = "The unique identifier of the order") @PathVariable String orderId) {

        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order.toResponseWithHistory());
    }
}
