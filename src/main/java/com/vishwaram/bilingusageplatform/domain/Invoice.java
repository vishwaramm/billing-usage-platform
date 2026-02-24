package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="invoices")
public class Invoice extends TenantEntityBase {

    public enum Status { DRAFT, FINALIZED }

    @Column(name="subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(name="period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name="period_end", nullable = false)
    private LocalDate periodEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String currency = "USD";

    @Column(name="total_cents", nullable = false)
    private long totalCents;

    // created_at already handled by BaseTenantEntity (Instant createdAt). If you prefer DB default, keep as-is.

    public UUID getSubscriptionId() { return subscriptionId; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public Status getStatus() { return status; }
    public String getCurrency() { return currency; }
    public long getTotalCents() { return totalCents; }

    public void setSubscriptionId(UUID subscriptionId) { this.subscriptionId = subscriptionId; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public void setStatus(Status status) { this.status = status; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setTotalCents(long totalCents) { this.totalCents = totalCents; }
}
