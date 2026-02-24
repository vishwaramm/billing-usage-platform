package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "usage_events",
        uniqueConstraints = @UniqueConstraint( name="uq_ue_idempo", columnNames = {"tenant_id", "idempotency_key"})
)
public class UsageEvent extends TenantEntityBase {
    @Column(name="subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(name="idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private String metric;

    @Column(nullable = false)
    private long quantity;

    @Column(name="occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name="billed_invoice_id")
    private UUID billedInvoiceId;

    public UUID getBilledInvoiceId() { return billedInvoiceId; }
    public void setBilledInvoiceId(UUID billedInvoiceId) { this.billedInvoiceId = billedInvoiceId; }

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(UUID subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
