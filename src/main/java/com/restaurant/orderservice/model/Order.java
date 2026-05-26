package com.restaurant.orderservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Entity  — tells JPA "this class is a database table"
 * @Table   — the table will be named "orders" in the database
 * @Id      — marks orderId as the primary key (unique identifier for each row)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "ordered_dish", nullable = false)
    private String orderedDish;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "order_price", nullable = false)
    private BigDecimal orderPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;
}