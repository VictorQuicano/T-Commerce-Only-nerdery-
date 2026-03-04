package com.tcommerce.TCommerce.application.controllers.sales;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.PaymentService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.CheckoutRequest;
import com.tcommerce.TCommerce.interfaces.dto.sales.CheckoutResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.PAYMENTS)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sales & Checkout", description = "Endpoints for managing the shopping cart and placing orders")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
        summary = "Initiate checkout",
        description = "Creates a Stripe Payment Intent for a specific order.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Payment intent created successfully",
                         content = @Content(schema = @Schema(implementation = CheckoutResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CheckoutResponse> checkout(
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CheckoutResponse response = paymentService.createPaymentIntent(
                request.orderId(),
                userDetails.getId()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Stripe Webhook",
        description = "Public endpoint for Stripe to send asynchronous event notifications (e.g., payment succeeded).",
        responses = {
            @ApiResponse(responseCode = "200", description = "Webhook received successfully")
        }
    )
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            HttpServletRequest request,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            byte[] payload = request.getInputStream().readAllBytes();
            paymentService.handleWebhook(payload, sigHeader);
        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage());
            // Always return 200 to Stripe to prevent retries
        }

        return ResponseEntity.ok().build();
    }
}
