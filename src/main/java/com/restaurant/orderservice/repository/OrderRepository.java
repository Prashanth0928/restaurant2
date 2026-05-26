package com.restaurant.orderservice.repository;

import com.restaurant.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository gives us all database operations for FREE:
 *
 *   save(order)        → INSERT or UPDATE into database
 *   findById(id)       → SELECT * FROM orders WHERE order_id = ?
 *   findAll()          → SELECT * FROM orders
 *   deleteById(id)     → DELETE FROM orders WHERE order_id = ?
 *   existsById(id)     → check if a row exists
 *
 * No need to write SQL — Spring Data JPA handles it automatically!
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
