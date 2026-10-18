package com.tech.shoeshop.publisher;

import com.tech.shoeshop.event.ClaimedOutboxEvent;
import com.tech.shoeshop.repository.event.OutboxClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final OutboxClaimRepository outboxClaimRepositoryRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(
            fixedDelayString = "${outbox.publisher.fixed-delay:5000}"
    )
    public void publishPendingEvents(){
        var events = outboxClaimRepositoryRepository.claimBatch();

        for (ClaimedOutboxEvent event : events){
            publish(event);
        }
    }

    private void publish(ClaimedOutboxEvent event) {
        try {
            var result = kafkaTemplate.send(ORDER_CREATED_TOPIC, event.aggregatedId().toString(), event.payload())
                    .get(10, TimeUnit.SECONDS);

            int updated = outboxClaimRepositoryRepository.markPublished(event.id(), event.claimToken());

            if (updated == 1) {
                log.info(
                        "Published outbox event={}, partition={}, offset={}",
                        event.id(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            } else {
                log.warn(
                        "Event {} was sent but claim is no longer owned",
                        event.id()
                );
            }

        } catch (Exception ex) {
            log.error(
                    "Failed to publish outbox event={}. Will retry after lease expires.",
                    event.id(),
                    ex
            );

            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
