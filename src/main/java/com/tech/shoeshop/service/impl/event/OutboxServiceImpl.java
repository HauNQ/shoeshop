package com.tech.shoeshop.service.impl.event;

import tools.jackson.databind.json.JsonMapper;
import com.tech.shoeshop.entity.event.OutboxEvent;
import com.tech.shoeshop.entity.order.Order;
import com.tech.shoeshop.event.OrderCreatedEvent;
import com.tech.shoeshop.repository.event.OutboxRepository;
import com.tech.shoeshop.service.event.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;
    private final JsonMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void saveOrderCreatedEvent(Order order) {
        UUID eventId = UUID.randomUUID();

        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId,
                order.getId(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                Instant.now()
        );

        try {

            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.builder()
                    .id(eventId)
                    .aggregateId(order.getId())
                    .eventType("OrderCreated")
                    .payload(payload)
                    .createdAt(Instant.now())
                    .build();

            outboxRepository.save(outbox);

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Failed to serialize OrderCreatedEvent",
                    ex
            );
        }
    }
}
