package com.tech.shoeshop.repository.event;

import com.tech.shoeshop.entity.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository
        extends JpaRepository<OutboxEvent, UUID> {
}
