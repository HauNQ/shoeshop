package com.tech.shoeshop.dto.request.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemRequest {
    @NotNull(message = "Product Id must be required")
    @Positive(message = "Product Id must be greater than 0")
    private Long productId;

    @NotNull(message = "Quantity must be required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
