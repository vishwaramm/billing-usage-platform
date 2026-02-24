package com.vishwaram.bilingusageplatform.service;

import com.vishwaram.bilingusageplatform.api.dto.CreateUsageEventRequest;
import com.vishwaram.bilingusageplatform.domain.Subscription;
import com.vishwaram.bilingusageplatform.domain.UsageEvent;
import com.vishwaram.bilingusageplatform.infra.TenantContext;
import com.vishwaram.bilingusageplatform.repository.SubscriptionRepository;
import com.vishwaram.bilingusageplatform.repository.UsageEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsageEventService {
    private final SubscriptionRepository subscriptionRepository;
    private final UsageEventRepository usageEventRepository;

    public UsageEventService(SubscriptionRepository subscriptionRepository, UsageEventRepository usageEventRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.usageEventRepository = usageEventRepository;
    }

    @Transactional
    public UsageEvent create(CreateUsageEventRequest request) {
        UUID tenantId = TenantContext.getRequired();
        Subscription sub = subscriptionRepository.findByTenantIdAndId(tenantId, request.subscriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (sub.getStatus() != Subscription.Status.ACTIVE) {
            throw new IllegalArgumentException("Subscription is NOT Active");
        }

        Optional<UsageEvent> existing = usageEventRepository
                .findByTenantIdAndIdempotencyKey(tenantId, request.idempotencyKey());

        if (existing.isPresent()) {
            return existing.get();
        }

        UsageEvent usageEvent = new UsageEvent();
        usageEvent.setTenantId(tenantId);
        usageEvent.setSubscriptionId(request.subscriptionId());
        usageEvent.setIdempotencyKey(request.idempotencyKey());
        usageEvent.setMetric(request.metric());
        usageEvent.setQuantity(request.quantity());
        usageEvent.setOccurredAt(request.occurredAt());
        try {
            return usageEventRepository.saveAndFlush(usageEvent);
        } catch (DataIntegrityViolationException dup) {
            return usageEventRepository.findByTenantIdAndIdempotencyKey(tenantId, request.idempotencyKey())
                    .orElseThrow(() -> new IllegalArgumentException("Duplicate idempotencyKey but row not found."));
        }
    }
}
