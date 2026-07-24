package com.tech.shoeshop.mapper;

import com.tech.shoeshop.dto.response.order.OrderResponse;
import com.tech.shoeshop.entity.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = OrderItemMapper.class
)
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "createdAt", target = "orderDate")
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toResponse(Order order);
}
