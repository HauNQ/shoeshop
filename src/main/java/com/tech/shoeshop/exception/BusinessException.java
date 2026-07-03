package com.tech.shoeshop.exception;

public class BusinessException extends RuntimeException {
    public BusinessException (String message){
        super(message);
    }
}
