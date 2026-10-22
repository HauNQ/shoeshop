package com.tech.shoeshop.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaErrorHandlerConfig {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                                                                            (record, exception) ->
                                                                                new TopicPartition(
                                                                                            record.topic() + ".DLT",
                                                                                        record.partition()
                                                                                )
                                                                            );
        // 3 retries, spaced 2 seconds apart
        FixedBackOff backOff = new FixedBackOff(2000, 3L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Do not retry malformed JSON
        errorHandler.addNotRetryableExceptions(com.fasterxml.jackson.core.JsonProcessingException.class);

        return errorHandler;
    }
}
