package com.tech.shoeshop.mapper;

import com.tech.shoeshop.dto.response.order.OrderItemResponse;
import com.tech.shoeshop.entity.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);
}
