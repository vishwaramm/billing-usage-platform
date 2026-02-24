package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.UsageEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsageEventRepository extends JpaRepository<UsageEvent, UUID> {
    Optional<UsageEvent> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);

    interface MetricSum {
        String getMetric();
        long getTotalQuantity();
    }

    @Query(value ="""
        SELECT 
                metric as metric,
                COALESCE(SUM(quantity), 0) as totalQuantity,
            FROM usage_events
            WHERE tenant_id = :tenantId
            AND subscription_id = :subId
            AND occurred_at >= :startTs
            AND occurred_at < :endTs
            AND billed_invoice_id IS NULL
        GROUP BY metric
    """, nativeQuery = true)
    List<MetricSum> sumUnbilledByMetric(
            @Param("tenantId") UUID tenantId,
            @Param("subId") UUID subscriptionId,
            @Param("startTs") Instant startTs,
            @Param("endTs") Instant endTs
    );

    @Modifying
    @Query(value = """
        UPDATE usage_events
            SET billed_invoice_id = :invoiceId
            WHERE tenant_id = :tenantId
            AND subscription_id = :subId
            AND occurred_at >= :startTs
            AND occurred_at < :endTs
            AND billed_invoice_id IS NULL
    """, nativeQuery = true)
    int markBilled(
            @Param("invoiceId") UUID invoiceId,
            @Param("tenantId") UUID tenantId,
            @Param("subId") UUID subscriptionId,
            @Param("startTs") Instant startTs,
            @Param("endTs") Instant endTs
    );
}
