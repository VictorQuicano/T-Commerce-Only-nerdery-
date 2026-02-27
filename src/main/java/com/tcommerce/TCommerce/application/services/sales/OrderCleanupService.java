package com.tcommerce.TCommerce.application.services.sales;

import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatusHistory;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final ChangeStatusNotificationService changeStatusNotificationService;

    /**
     * Executes every day at midnight (system time).
     * Cancels orders that have been in AWAITING_PAYMENT status for more than 24 hours
     * and returns the items' quantities to the product stock.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cancelUnpaidOrders() {
        log.info("Starting automated cleanup of unpaid orders");
        
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Order> orders = orderRepository.findByStatusAndUpdatedAtBefore(OrderStatus.AWAITING_PAYMENT, oneDayAgo);
        
        if (orders.isEmpty()) {
            log.info("No unpaid orders found to cancel.");
            return;
        }

        log.info("Found {} orders awaiting payment for more than 24 hours", orders.size());

        orders.forEach(order -> {
            String reason = "Automatically cancelled due to non-payment after 24 hours";
            order = orderService.cancelOrder(order, "SYSTEM", reason);
        });
        
        log.info("Automated cleanup of unpaid orders completed");
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
}
