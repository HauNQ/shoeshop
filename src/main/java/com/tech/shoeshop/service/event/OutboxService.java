package com.tech.shoeshop.service.event;

import com.tech.shoeshop.entity.order.Order;

public interface OutboxService {
    void saveOrderCreatedEvent(Order order);
}
