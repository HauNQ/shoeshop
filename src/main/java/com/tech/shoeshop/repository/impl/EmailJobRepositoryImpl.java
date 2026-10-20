package com.tech.shoeshop.repository.impl;

import com.tech.shoeshop.repository.event.EmailJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmailJobRepositoryImpl implements EmailJobRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(UUID eventId, Long orderId, String recipient) {
        String sql = """
                    INSERT INTO email_jobs (
                        id,
                        event_id,
                        order_id,
                        recipient,
                        status
                    )
                    VALUES (?, ?, ?, ?, 'PENDING')
                """;

        jdbcTemplate.update(sql, UUID.randomUUID(), eventId, orderId, recipient);
    }
}
