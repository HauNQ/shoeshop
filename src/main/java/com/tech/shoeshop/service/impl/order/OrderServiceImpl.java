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
import com.tech.shoeshop.exception.DuplicateProductException;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final AuthService authService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse findOrderById(Long id) {

        Order order = orderRepository.findOrderById(id).orElseThrow(() ->{
            log.warn("Order with id {} not found", id);
            return new ResourceNotFoundException("Not found Order with Id " + id);
        });

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse findOrderDetailById(Long id) {
        Order order = orderRepository.findOrderDetailById(id).orElseThrow(() ->{
            log.warn("Order with id {} not found", id);
            return new ResourceNotFoundException("Not found Order with Id " + id);
        });

        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> findAll() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> findAllWithDetails() {
        List<Order> orders = orderRepository.findAllWithDetails();

        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }


    //    @Retryable(
//            retryFor = ObjectOptimisticLockingFailureException.class,
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 100)
//    )
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {

        User user = authService.getCurrentUser();

        log.debug("Creating an order for user {}", user.getUsername());

        validateDuplicateProducts(orderRequest.getItems());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        orderRequest.getItems().sort(
                Comparator.comparing(OrderItemRequest::getProductId)
        );

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

        int updatedRow = productRepository.deductStock(orderItemRequest.getProductId(), orderItemRequest.getQuantity());

        if(updatedRow <= 0){
            log.warn("Insufficient stock for product {}", orderItemRequest.getProductId());
            throw new InsufficientStockException("Insufficient stock for product " + orderItemRequest.getProductId());
        }

        //Apply pessimistic lock to get product
//        Product product = productRepository.findByIdWithPessimisticLock(orderItemRequest.getProductId())
//                .orElseThrow(() ->
//                {
//                    log.warn("Product with id {} not found", orderItemRequest.getProductId());
//                    return new ResourceNotFoundException("Product not found with id " + orderItemRequest.getProductId());
//                });

//        if(product.getStockQuantity() < orderItemRequest.getQuantity()){
//            log.warn("Insufficient stock for product {}", product.getId());
//            throw new InsufficientStockException("Insufficient stock for product " + product.getId());
//        }

        //Add sleep to test race limit
//        try{
//            Thread.sleep(500);
//        }catch (InterruptedException e){
//            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
//        }

//        product.deductInventory(orderItemRequest.getQuantity());

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

    private void validateDuplicateProducts(List<OrderItemRequest> items){
        Map<Long, Long> count = items.stream()
                .collect(Collectors
                        .groupingBy(
                                OrderItemRequest::getProductId,
                                Collectors.counting()));

        count.forEach((id, c) -> {
            if(c > 1){
                throw new DuplicateProductException("Duplicate product id: "+ id);
            }
        } );
    }
}
