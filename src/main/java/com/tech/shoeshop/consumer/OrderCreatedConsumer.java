package com.tech.shoeshop.consumer;

import com.tech.shoeshop.event.OrderCreatedEvent;
import com.tech.shoeshop.service.event.EmailEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final ObjectMapper objectMapper;
    private final EmailEventService emailEventService;

    @KafkaListener(
            topics = "order-created",
            groupId = "shoeshop-email-group"
    )
    public void consumer(String message){
        OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

        log.info("Received OrderCreatedEvent: orderId={}, eventId={}", event.orderId(), event.eventId());

        emailEventService.process(event);
    }
}
