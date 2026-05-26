package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.model.OrderStatus;
import com.restaurant.orderservice.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO returned to clients in all successful order API responses.
 *
 * Separating the response DTO from the domain model means we can add/remove
 * response fields independently of what's stored internally — e.g., computed
 * fields, formatted values, or hidden sensitive data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order details returned in API responses")
public class OrderResponseDTO {

    @Schema(description = "Unique identifier for the order", example = "ORD-A1B2C3D4")
    private String orderId;

    @Schema(description = "Full name of the customer", example = "John Doe")
    private String customerName;

    @Schema(description = "Name of the dish ordered", example = "Margherita Pizza")
    private String orderedDish;

    @Schema(description = "Number of dishes ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Total price of the order in USD", example = "25.99")
    private BigDecimal orderPrice;

    @Schema(description = "Current status of the order", example = "PENDING")
    private OrderStatus orderStatus;

    @Schema(description = "Name of the restaurant", example = "Mario's Italian Kitchen")
    private String restaurantName;

    @Schema(description = "Timestamp when the order was placed", example = "2024-05-22T10:30:00")
    private LocalDateTime orderTime;

    @Schema(description = "Delivery address for the order", example = "123 Main Street, Springfield, IL 62701")
    private String deliveryAddress;

    @Schema(description = "Current payment status of the order", example = "PENDING")
    private PaymentStatus paymentStatus;
}