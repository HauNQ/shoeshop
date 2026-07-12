package com.tech.shoeshop.dto.response.product;

import com.tech.shoeshop.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Response object returned when retrieving product information
 * */
@Getter
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private Status status;
}
