package com.tech.shoeshop.controller;

import com.tech.shoeshop.common.response.ApiResponse;
import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.dto.response.order.OrderResponse;
import com.tech.shoeshop.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest){
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
}
