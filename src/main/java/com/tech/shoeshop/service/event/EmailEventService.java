package com.tech.shoeshop.service.event;

import com.tech.shoeshop.event.OrderCreatedEvent;

public interface EmailEventService {
    void process(OrderCreatedEvent event);
}
