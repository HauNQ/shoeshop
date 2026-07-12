package com.tech.shoeshop.dto.request.product;

import com.tech.shoeshop.enums.Status;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request object used when creating or updating a product.
 * */
@Getter
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "product name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be at least 0,01")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @NotNull(message = "Product quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer stockQuantity;

    @NotNull(message = "Category Id is required")
    @Positive(message = "Category Id must be positive value")
    private Long categoryId;

    @NotNull(message = "Status is required")
    private Status status;
}
