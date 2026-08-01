package com.tech.shoeshop.exception;

public class DuplicateProductException extends BusinessException{
    public DuplicateProductException(String message) {
        super(message);
    }
}
