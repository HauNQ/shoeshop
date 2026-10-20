package com.tech.shoeshop.repository.event;

import java.util.UUID;

public interface EmailJobRepository {
    void create(
            UUID eventId,
            Long orderId,
            String recipient
    );
}
