package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription extends TenantEntityBase {
    public enum Status { ACTIVE, PAUSED, CANCELED }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "plan_id")
    private UUID planId;

    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }

    public Status getStatus() { return this.status; }
    public void setStatus(Status status) {
        this.status = status;
    }

    public UUID getSubscriptionId() {
        return this.getId();
    }
}
