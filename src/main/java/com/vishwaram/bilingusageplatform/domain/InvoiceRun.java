package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "invoice_runs",
        uniqueConstraints = @UniqueConstraint(
                name="uq_run_tenant_period",
                columnNames={"tenant_id","period_start","period_end"}
        )
)
public class InvoiceRun extends TenantEntityBase {

    public enum Status { STARTED, COMPLETED }

    @Column(name="period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name="period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="completed_at")
    private Instant completed_at;

    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }


    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCompleted_at(Instant completed_at) {
        this.completed_at = completed_at;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCompleted_at() {
        return completed_at;
    }
}
