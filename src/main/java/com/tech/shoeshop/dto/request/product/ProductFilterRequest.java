package com.tech.shoeshop.dto.request.product;

import com.tech.shoeshop.enums.Status;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductFilterRequest {
    private String name;

    private String categoryName;

    @DecimalMin(value = "0.01", message = "Product price must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.01", message = "Product price must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    private BigDecimal maxPrice;

    private Status status;
}
