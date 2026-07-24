package com.tech.shoeshop.service.impl.order;

import com.tech.shoeshop.dto.request.order.OrderItemRequest;
import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.dto.response.order.OrderResponse;
import com.tech.shoeshop.entity.auth.User;
import com.tech.shoeshop.entity.order.Order;
import com.tech.shoeshop.entity.order.OrderItem;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.OrderStatus;
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

        for(OrderItemRequest item : orderRequest.getItems()){
            order.addOrderItem(convertToOrderItem(item));
        }

        order = orderRepository.save(order);

        log.debug("Order {} created successfully for user {}", order.getId(), user.getUsername());

        return orderMapper.toResponse(order);
    }

    private OrderItem convertToOrderItem(OrderItemRequest orderItemRequest){

        Product product = productRepository.findById(orderItemRequest.getProductId())
                .orElseThrow(() ->
                {
                    log.warn("Product with id {} not found", orderItemRequest.getProductId());
                    return new ResourceNotFoundException("Product not found with id " + orderItemRequest.getProductId());
                });

        return OrderItem.builder()
                .product(product)
                .quantity(orderItemRequest.getQuantity())
                .unitPrice(product.getPrice())
                .subTotal(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())))
                .build();
    }
}
