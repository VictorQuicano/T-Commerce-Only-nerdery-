package com.tcommerce.TCommerce.application.controllers.sales;

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
public class PaymentController {

    private final PaymentService paymentService;

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
