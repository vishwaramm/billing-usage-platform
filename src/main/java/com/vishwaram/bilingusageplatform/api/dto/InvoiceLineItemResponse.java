package com.vishwaram.bilingusageplatform.api.dto;

import java.util.UUID;

public record InvoiceLineItemResponse(
    UUID id,
    String kind,
    String metric,
    String description,
    long quantity,
    long unitPriceCents,
    long amountCents
) {}
