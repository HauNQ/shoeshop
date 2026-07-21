package com.tech.shoeshop.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    @JsonValue
    public String toJson(){
        return name().toLowerCase();
    }

    @JsonCreator
    public OrderStatus fromJson(String value){
        return Arrays.stream(values())
                .filter(orderStatus -> orderStatus.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status: " + value));
    }
}
