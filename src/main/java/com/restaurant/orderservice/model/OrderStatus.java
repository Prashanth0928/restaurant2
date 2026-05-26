package com.restaurant.orderservice.model;

/**
 * Represents the lifecycle status of a restaurant order.
 */
public enum OrderStatus {
    PENDING,           // Order placed, not yet confirmed
    CONFIRMED,         // Restaurant confirmed the order
    PREPARING,         // Kitchen is preparing the food
    OUT_FOR_DELIVERY,  // Order is on the way
    DELIVERED,         // Order delivered to customer
    CANCELLED          // Order was cancelled
}
