package com.tech.shoeshop.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(UUID eventId,
                                Long orderId,
                                String customerEmail,
                                BigDecimal totalAmount,
                                Instant occurredAt
) {
}
