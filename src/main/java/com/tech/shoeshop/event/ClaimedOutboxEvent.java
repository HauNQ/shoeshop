package com.tech.shoeshop.event;

import java.util.UUID;

public record ClaimedOutboxEvent (
        UUID id,
        Long aggregatedId,
        String payload,
        UUID claimToken
){
}
