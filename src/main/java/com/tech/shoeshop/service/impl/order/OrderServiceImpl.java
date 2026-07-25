package com.tech.shoeshop.service.impl.order;

import com.tech.shoeshop.dto.request.order.OrderItemRequest;
import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.dto.request.order.OrderStatusRequest;
import com.tech.shoeshop.dto.response.order.OrderResponse;
import com.tech.shoeshop.entity.auth.User;
import com.tech.shoeshop.entity.order.Order;
import com.tech.shoeshop.entity.order.OrderItem;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.OrderStatus;
import com.tech.shoeshop.enums.Status;
import com.tech.shoeshop.exception.InsufficientStockException;
import com.tech.shoeshop.exception.InvalidStatusTransition;
import com.tech.shoeshop.exception.ResourceNotFoundException;
import com.tech.shoeshop.mapper.OrderMapper;
import com.tech.shoeshop.repository.order.OrderRepository;
import com.tech.shoeshop.repository.product.ProductRepository;
import com.tech.shoeshop.service.auth.AuthService;
import com.tech.shoeshop.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final AuthService authService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {

        User user = authService.getCurrentUser();

        log.debug("Creating an order for user {}", user.getUsername());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequest item : orderRequest.getItems()) {
            order.addOrderItem(convertToOrderItem(item));
        }

        order = orderRepository.save(order);

        log.debug("Order {} created successfully for user {}", order.getId(), user.getUsername());

        return orderMapper.toResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->{
                    log.warn("Order with id {} not found", id);
                    return new ResourceNotFoundException("Not found Order with Id " + id);
                });

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = orderStatusRequest.getOrderStatus();

        log.debug("Updating order {} from {} to {}", id, currentStatus, newStatus);

        if(!canTransition(currentStatus, newStatus)){
            throw new InvalidStatusTransition(currentStatus.name(), newStatus.name());
        }

        order.setStatus(newStatus);

        log.debug("Order {} status changed from {} to {}", order.getId(), currentStatus, newStatus);

        return orderMapper.toResponse(order);
    }

    private OrderItem convertToOrderItem(OrderItemRequest orderItemRequest) {

        Product product = productRepository.findById(orderItemRequest.getProductId())
                .orElseThrow(() ->
                {
                    log.warn("Product with id {} not found", orderItemRequest.getProductId());
                    return new ResourceNotFoundException("Product not found with id " + orderItemRequest.getProductId());
                });

        if(product.getStockQuantity() < orderItemRequest.getQuantity()){
            log.warn("Insufficient stock for product {}", product.getId());
            throw new InsufficientStockException("Insufficient stock for product " + product.getId());
        }

        product.deductInventory(orderItemRequest.getQuantity());

        return OrderItem.builder()
                .product(product)
                .quantity(orderItemRequest.getQuantity())
                .unitPrice(product.getPrice())
                .subTotal(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())))
                .build();
    }

    private boolean canTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
