package com.vishwaram.bilingusageplatform.api.dto;

import java.util.List;
import java.util.UUID;

public record RunInvoicesResponse(
    String periodStart,
    String periodEnd,
    int createdCount,
    List<UUID> invoiceIds) {}
