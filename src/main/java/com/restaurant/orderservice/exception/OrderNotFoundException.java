package com.restaurant.orderservice.exception;

/**
 * Thrown by the service layer when a requested order does not exist.
 * Caught by GlobalExceptionHandler and mapped to HTTP 404.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}