package com.tech.shoeshop.repository.impl;

import com.tech.shoeshop.event.ClaimedOutboxEvent;
import com.tech.shoeshop.repository.event.OutboxClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxClaimRepositoryImpl implements OutboxClaimRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<ClaimedOutboxEvent> claimBatch() {
        UUID token = UUID.randomUUID();

        String sql = """
                    WITH selected AS (
                        SELECT id
                        FROM outbox_events
                        WHERE published_at IS NULL
                            AND (locked_until IS NULL OR locked_until < CURRENT_TIMESTAMP)
                        ORDER BY created_at, id
                        LIMIT 20
                        FOR UPDATE SKIP LOCKED
                    )
                    UPDATE outbox_events AS o
                    SET claim_token = ?,
                        locked_until = CURRENT_TIMESTAMP + INTERVAL '60 seconds',
                        attempts = attempts + 1
                    FROM selected
                    WHERE o.id = selected.id
                    RETURNING o.id, o.aggregate_id, o.payload, o.claim_token
                """;



        return jdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> new ClaimedOutboxEvent(
                                rs.getObject("id", UUID.class),
                                rs.getLong("aggregate_id"),
                                rs.getString("payload"),
                                rs.getObject("claim_token", UUID.class)
                    ),
                    token
        );
    }

    @Override
    public int markPublished(UUID eventId, UUID claimToken) {
        String sql = """
                    UPDATE outbox_events
                    SET published_at = CURRENT_TIMESTAMP,
                        locked_until = NULL,
                        claim_token = NULL
                    WHERE id = ?
                        AND claim_token = ?
                        AND published_at IS NULL
                """;
        return jdbcTemplate.update(sql, eventId, claimToken);
    }
}
