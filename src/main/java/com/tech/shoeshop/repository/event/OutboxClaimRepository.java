package com.tech.shoeshop.repository.event;

import com.tech.shoeshop.event.ClaimedOutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxClaimRepository  {
    List<ClaimedOutboxEvent> claimBatch();
    int markPublished(UUID eventId, UUID claimToken);
}
