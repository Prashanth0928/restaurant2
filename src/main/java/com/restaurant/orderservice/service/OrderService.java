package com.restaurant.orderservice.service;

import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;

import java.util.List;

/**
 * Service interface declaring all order business operations.
 *
 * Keeping an interface here provides the same abstraction benefits as
 * the repository interface: the controller depends on this contract,
 * not on the concrete implementation. This enables easy unit testing
 * with mocked implementations.
 */
public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO requestDTO);

    OrderResponseDTO getOrderById(String orderId);

    OrderResponseDTO updateOrder(String orderId, OrderRequestDTO requestDTO);

    void deleteOrder(String orderId);

    List<OrderResponseDTO> getAllOrders();
}