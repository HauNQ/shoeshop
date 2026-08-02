package com.tech.shoeshop.service.order;

import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.dto.request.order.OrderStatusRequest;
import com.tech.shoeshop.dto.response.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse findOrderById(Long id);
    OrderResponse findOrderDetailById(Long id);
    List<OrderResponse> findAll();
    List<OrderResponse> findAllWithDetails();
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest);
}
