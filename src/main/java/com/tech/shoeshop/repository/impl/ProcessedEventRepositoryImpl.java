package com.tech.shoeshop.repository.impl;

import com.tech.shoeshop.repository.event.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryImpl implements ProcessedEventRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean tryMarkedProcessed(String consumerName, UUID eventId) {
        String sql = """
                    INSERT INTO processed_events (
                        consumer_name,
                        event_id
                    )
                    VALUES (?, ?)
                    ON CONFLICT DO NOTHING
                """;
        int rows = jdbcTemplate.update(sql, consumerName, eventId);
        return rows == 1;
    }
}
