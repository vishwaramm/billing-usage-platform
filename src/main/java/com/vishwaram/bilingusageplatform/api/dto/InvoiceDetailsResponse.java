package com.vishwaram.bilingusageplatform.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceDetailsResponse(
       UUID id,
       UUID subscriptionId,
       LocalDate periodStart,
       LocalDate periodEnd,
       String status,
       String currency,
       long totalCents,
       List<InvoiceLineItemResponse> invoiceLineItemResponses
) {}
