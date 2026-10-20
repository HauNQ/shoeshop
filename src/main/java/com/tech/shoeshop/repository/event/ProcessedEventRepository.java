package com.tech.shoeshop.repository.event;

import java.util.UUID;

public interface ProcessedEventRepository {
    boolean tryMarkedProcessed(String consumerName, UUID eventId);
}
