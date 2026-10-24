package com.tech.shoeshop.consumer;

import com.tech.shoeshop.event.OrderCreatedEvent;
import com.tech.shoeshop.service.event.EmailEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
            groupId = "shoeshop-email-group",
            concurrency = "3"
    )
    public void consumer(String message,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key,
                         @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                         @Header(KafkaHeaders.OFFSET) long offset){

        log.info(
                "Thread={}, Topic={}, Key={}, Partition={}, Offset={}",
                Thread.currentThread().getName(),
                "order-created",
                key,
                partition,
                offset
        );

        OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

        log.info("Received OrderCreatedEvent: orderId={}, eventId={}", event.orderId(), event.eventId());

        emailEventService.process(event);
    }
}
