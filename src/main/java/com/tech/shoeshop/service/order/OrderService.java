package com.tech.shoeshop.service.order;

import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.dto.request.order.OrderStatusRequest;
import com.tech.shoeshop.dto.response.order.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest);
}
