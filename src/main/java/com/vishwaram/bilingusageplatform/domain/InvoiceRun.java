package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.*;
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

    @Column(name="period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name="period_end", nullable = false)
    private LocalDate periodEnd;

    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }

    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
}
