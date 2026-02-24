package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name="plan_metered_prices")
public class PlanMeteredPrice extends TenantEntityBase {

    @Column(name="plan_id", nullable = false)
    private UUID planId;

    @Column(nullable = false)
    private String metric;

    @Column(name = "unit_price_cents", nullable = false)
    private long unitPriceCents;

    public UUID getPlanId() { return planId; }
    public String getMetric() { return metric; }
    public long getUnitPriceCents() { return unitPriceCents; }

    public void setPlanId(UUID planId) { this.planId = planId; }
    public void setMetric(String metric) { this.metric = metric; }
    public void setUnitPriceCents(long unitPriceCents) { this.unitPriceCents = unitPriceCents; }
}
