package com.ust_internal.productdiscountapi.exception;

public class InvalidProductException extends RuntimeException {
    public InvalidProductException(String message) {
        super(message);
    }
    
    public InvalidProductException(String message, Throwable cause) {
        super(message, cause);
    }
}