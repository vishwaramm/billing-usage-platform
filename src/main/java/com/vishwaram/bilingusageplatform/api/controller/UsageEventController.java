package com.vishwaram.bilingusageplatform.api.controller;

import com.vishwaram.bilingusageplatform.api.dto.CreateUsageEventRequest;
import com.vishwaram.bilingusageplatform.api.dto.UsageEventResponse;
import com.vishwaram.bilingusageplatform.domain.UsageEvent;
import com.vishwaram.bilingusageplatform.service.UsageEventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/usage-events")
public class UsageEventController {
    public final UsageEventService usageEventService;

    public UsageEventController(UsageEventService usageEventService) {
        this.usageEventService = usageEventService;
    }

    @PostMapping
    public ResponseEntity<UsageEventResponse> create(@Valid @RequestBody CreateUsageEventRequest request) {
        UsageEvent event = usageEventService.create(request);
        return ResponseEntity.ok(toResponse(event));
    }

    private static UsageEventResponse toResponse(UsageEvent e) {
        return new UsageEventResponse(
                e.getId(),
                e.getTenantId(),
                e.getSubscriptionId(),
                e.getIdempotencyKey(),
                e.getMetric(),
                e.getQuantity(),
                e.getOccurredAt()
        );
    }
}
