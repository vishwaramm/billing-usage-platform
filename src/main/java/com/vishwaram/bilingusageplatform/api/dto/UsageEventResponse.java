package com.vishwaram.bilingusageplatform.api.dto;

import java.time.Instant;
import java.util.UUID;

public record UsageEventResponse(
        UUID id,
        UUID tenantId,
        UUID subscriptionId,
        String idempotencyKey,
        String metric,
        long quantity,
        Instant occurredAt
) {
}
