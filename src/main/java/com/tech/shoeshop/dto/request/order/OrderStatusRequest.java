package com.tech.shoeshop.dto.request.order;

import com.tech.shoeshop.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus orderStatus;
}
