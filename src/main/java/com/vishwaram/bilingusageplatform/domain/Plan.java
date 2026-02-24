package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name="plans")
public class Plan extends TenantEntityBase{

    @Column(nullable = false)
    private String name;

    @Column(name ="base_fee_cents", nullable = false)
    private long baseFeeCents;

    @Column(nullable = false)
    private String currency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBaseFeeCents() {
        return baseFeeCents;
    }

    public void setBaseFeeCents(long baseFeeCents) {
        this.baseFeeCents = baseFeeCents;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
