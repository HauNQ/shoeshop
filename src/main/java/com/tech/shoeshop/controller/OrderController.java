package com.tech.shoeshop.controller;

import com.tech.shoeshop.common.response.ApiResponse;
import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.dto.request.order.OrderStatusRequest;
import com.tech.shoeshop.dto.response.order.OrderResponse;
import com.tech.shoeshop.service.order.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.createOrder(orderRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(response.getOrderId())
                .toUri();

        return ResponseEntity.created(location)
                .body(
                        ApiResponse
                                .success(
                                        HttpStatus.CREATED,
                                        "Order created successfully",
                                        response
                                )
                );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable("id")
            @Positive(message = "Order ID must be greater than 0")
            Long id,

            @Valid
            @RequestBody
            OrderStatusRequest orderStatusRequest
    ) {
        OrderResponse response = orderService.updateOrderStatus(id, orderStatusRequest);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Order status updated successfully",
                        response
                )
        );
    }
}
