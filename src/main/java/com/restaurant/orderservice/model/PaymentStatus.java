package com.restaurant.orderservice.model;

/**
 * Represents the payment state of a restaurant order.
 */
public enum PaymentStatus {
    PENDING,   // Payment not yet processed
    PAID,      // Payment successfully completed
    FAILED,    // Payment attempt failed
    REFUNDED   // Payment was refunded (e.g., after cancellation)
}
