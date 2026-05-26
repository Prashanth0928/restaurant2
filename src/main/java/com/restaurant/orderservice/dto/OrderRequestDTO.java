package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.model.OrderStatus;
import com.restaurant.orderservice.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) for incoming POST /orders and PUT /orders/{id} requests.
 *
 * DTOs act as a contract between the client and the API — they validate
 * user input before it reaches the service layer, keeping the domain model clean.
 *
 * Validation annotations throw MethodArgumentNotValidException when violated,
 * which is caught and formatted by GlobalExceptionHandler.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a restaurant order")
public class OrderRequestDTO {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    @Schema(description = "Full name of the customer", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @NotBlank(message = "Ordered dish is required")
    @Size(max = 200, message = "Dish name must not exceed 200 characters")
    @Schema(description = "Name of the dish being ordered", example = "Margherita Pizza", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderedDish;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    @Schema(description = "Number of dishes ordered", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotNull(message = "Order price is required")
    @DecimalMin(value = "0.01", message = "Order price must be greater than 0")
    @Schema(description = "Total price of the order in USD", example = "25.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal orderPrice;

    @Schema(description = "Current status of the order. Defaults to PENDING if not provided.", example = "PENDING")
    private OrderStatus orderStatus;

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 150, message = "Restaurant name must not exceed 150 characters")
    @Schema(description = "Name of the restaurant fulfilling the order", example = "Mario's Italian Kitchen", requiredMode = Schema.RequiredMode.REQUIRED)
    private String restaurantName;

    @NotBlank(message = "Delivery address is required")
    @Size(max = 300, message = "Delivery address must not exceed 300 characters")
    @Schema(description = "Full delivery address for the order", example = "123 Main Street, Springfield, IL 62701", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryAddress;

    @Schema(description = "Payment status for the order. Defaults to PENDING if not provided.", example = "PENDING")
    private PaymentStatus paymentStatus;
}
