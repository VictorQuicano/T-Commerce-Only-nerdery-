package com.tcommerce.TCommerce.application.controllers.manager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.application.query.OrderPaginationRequest;
import com.tcommerce.TCommerce.application.query.OrderFilter;
import com.tcommerce.TCommerce.application.services.common.PageProcessor;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderWithHistory;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.tcommerce.TCommerce.interfaces.dto.sales.UpdateOrderStatus;



@RestController
@RequestMapping(ApiPaths.V1 + "/manager/orders")
@PreAuthorize("hasAnyRole('MANAGER')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Administrative endpoints for managing products, categories, and orders")
@SecurityRequirement(name = "bearerAuth")
public class ManagerOrderController extends PageProcessor {
    private final OrderService orderService;

    @Operation(
        summary = "Get all orders (Admin View)",
        description = "Administrative endpoint to list and filter orders across all users.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
        }
    )
    @GetMapping
    public ResponseEntity<Window<OrderResponse>> getAllOrders(
            @Parameter(description = "Pagination and filter parameters") OrderPaginationRequest request) {
        OrderFilter filter = new OrderFilter(request.status(), request.userId());
        
        PaginationCriteria criteria = processRequest(request);

        ScrollPosition position = ScrollPosition.keyset();

        Sort sort = Sort.unsorted();
        if (request.sortBy() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.sortOrder()) 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, request.sortBy());
        }

        if (sort.isUnsorted()) {
             sort = Sort.by("id").descending();
        }
        
        int limit = criteria.limit();
        
        Window<Order> result = orderService.getAllOrders(filter, position, limit, sort);
        
        Window<OrderResponse> response = result.map(Order::toResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update order status",
        description = "Administrative endpoint to change the status of an order (e.g., from PAID to SHIPPED).",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                         content = @Content(schema = @Schema(implementation = OrderWithHistory.class))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager access required")
        }
    )
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderWithHistory> updateOrderStatus(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(description = "The unique identifier of the order") @PathVariable String orderId, 
        @Valid @RequestBody UpdateOrderStatus request) {
        String userId = userDetails.getId();
        String reason = request.reason();
        Order order = orderService.updateOrderStatus(orderId, request.status(), userId, reason);
        return ResponseEntity.ok(order.toResponseWithHistory());
    }   
}