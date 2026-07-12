package com.tech.shoeshop.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Status {
    ACTIVE,
    DELETED,
    PENDING,
    SOLD_OUT;

    @JsonValue
    public String toJson(){
        return name().toLowerCase();
    }

    @JsonCreator
    public static Status fromJson(String value){
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + value));
    }
}
