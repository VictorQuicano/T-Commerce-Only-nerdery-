package com.tcommerce.TCommerce.interfaces.dto.sales;

import jakarta.validation.constraints.Size;
public record CancelOrderRequest(

    @Size(min = 15, max = 255, message = "Reason must have at least 15 characters")
    String reason
 ) {
    
}
