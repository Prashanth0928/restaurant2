package com.restaurant.orderservice.service.impl;

import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.exception.OrderNotFoundException;
import com.restaurant.orderservice.model.Order;
import com.restaurant.orderservice.model.OrderStatus;
import com.restaurant.orderservice.model.PaymentStatus;
import com.restaurant.orderservice.repository.OrderRepository;
import com.restaurant.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        log.info("Creating new order for customer: {}", requestDTO.getCustomerName());

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderId(orderId)
                .customerName(requestDTO.getCustomerName())
                .orderedDish(requestDTO.getOrderedDish())
                .quantity(requestDTO.getQuantity())
                .orderPrice(requestDTO.getOrderPrice())
                .orderStatus(requestDTO.getOrderStatus() != null ? requestDTO.getOrderStatus() : OrderStatus.PENDING)
                .restaurantName(requestDTO.getRestaurantName())
                .orderTime(LocalDateTime.now())
                .deliveryAddress(requestDTO.getDeliveryAddress())
                .paymentStatus(requestDTO.getPaymentStatus() != null ? requestDTO.getPaymentStatus() : PaymentStatus.PENDING)
                .build();

        // .save() now writes to the H2 database — not just memory!
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved to database with ID: {}", orderId);

        return mapToResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(String orderId) {
        log.info("Fetching order from database with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found in database with ID: {}", orderId);
                    return new OrderNotFoundException("Order not found with ID: " + orderId);
                });

        return mapToResponseDTO(order);
    }

    @Override
    public OrderResponseDTO updateOrder(String orderId, OrderRequestDTO requestDTO) {
        log.info("Updating order in database with ID: {}", orderId);

        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        Order updatedOrder = Order.builder()
                .orderId(existingOrder.getOrderId())
                .customerName(requestDTO.getCustomerName())
                .orderedDish(requestDTO.getOrderedDish())
                .quantity(requestDTO.getQuantity())
                .orderPrice(requestDTO.getOrderPrice())
                .orderStatus(requestDTO.getOrderStatus() != null
                        ? requestDTO.getOrderStatus()
                        : existingOrder.getOrderStatus())
                .restaurantName(requestDTO.getRestaurantName())
                .orderTime(existingOrder.getOrderTime())
                .deliveryAddress(requestDTO.getDeliveryAddress())
                .paymentStatus(requestDTO.getPaymentStatus() != null
                        ? requestDTO.getPaymentStatus()
                        : existingOrder.getPaymentStatus())
                .build();

        Order savedOrder = orderRepository.save(updatedOrder);
        log.info("Order updated in database: {}", orderId);

        return mapToResponseDTO(savedOrder);
    }

    @Override
    public void deleteOrder(String orderId) {
        log.info("Deleting order from database with ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }

        orderRepository.deleteById(orderId);
        log.info("Order deleted from database: {}", orderId);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Fetching all orders from database");

        List<OrderResponseDTO> orders = orderRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        log.info("Total orders in database: {}", orders.size());
        return orders;
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .customerName(order.getCustomerName())
                .orderedDish(order.getOrderedDish())
                .quantity(order.getQuantity())
                .orderPrice(order.getOrderPrice())
                .orderStatus(order.getOrderStatus())
                .restaurantName(order.getRestaurantName())
                .orderTime(order.getOrderTime())
                .deliveryAddress(order.getDeliveryAddress())
                .paymentStatus(order.getPaymentStatus())
                .build();
    }
}