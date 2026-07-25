package com.tech.shoeshop.exception;

public class InvalidStatusTransition extends BusinessException{

    public InvalidStatusTransition(String currentStatus, String newStatus) {
        super("Cannot change status from " + currentStatus + " to " + newStatus);
    }
}
