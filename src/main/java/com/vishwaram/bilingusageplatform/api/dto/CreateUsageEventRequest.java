package com.vishwaram.bilingusageplatform.api.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.UUID;

public record CreateUsageEventRequest(
        @NotBlank String idempotencyKey,
        @NotNull UUID subscriptionId,
        @NotBlank String metric,
        @Positive long quantity,
        @NotNull Instant occurredAt
) {}
