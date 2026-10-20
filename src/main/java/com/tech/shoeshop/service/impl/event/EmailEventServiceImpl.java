package com.tech.shoeshop.service.impl.event;

import com.tech.shoeshop.event.OrderCreatedEvent;
import com.tech.shoeshop.repository.event.EmailJobRepository;
import com.tech.shoeshop.repository.event.ProcessedEventRepository;
import com.tech.shoeshop.service.event.EmailEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailEventServiceImpl implements EmailEventService {

    private static final String CONSUMER_NAME = "shoeshop-email";
    private final ProcessedEventRepository processedEventRepository;
    private final EmailJobRepository emailJobRepository;

    @Transactional
    @Override
    public void process(OrderCreatedEvent event) {
        boolean firstTime = processedEventRepository.tryMarkedProcessed(CONSUMER_NAME, event.eventId());

        if(!firstTime){
            log.info("Skipping duplicated eventId={}", event.eventId());
            return;
        }

        emailJobRepository.create( event.eventId(),
                event.orderId(),
                event.customerEmail()
        );

        log.info("Created email job for orderId={}, eventId={}", event.orderId(), event.eventId());
    }
}
