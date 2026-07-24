package com.tech.shoeshop.dto.response.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    private String productName;
    private Long productId;
}
