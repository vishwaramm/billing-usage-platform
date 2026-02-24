package com.vishwaram.bilingusageplatform.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "invoice_line_items")
public class InvoiceLineItem extends TenantEntityBase {

    public enum Kind { BASE, METERED }

    @Column(name="invoice_id", nullable = false)
    private UUID invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kind kind;

    @Column
    private String metric; // nullable for BASE

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private long quantity;

    @Column(name="unit_price_cents", nullable = false)
    private long unitPriceCents;

    @Column(name="amount_cents", nullable = false)
    private long amountCents;

    public UUID getInvoiceId() { return invoiceId; }
    public Kind getKind() { return kind; }
    public String getMetric() { return metric; }
    public String getDescription() { return description; }
    public long getQuantity() { return quantity; }
    public long getUnitPriceCents() { return unitPriceCents; }
    public long getAmountCents() { return amountCents; }

    public void setInvoiceId(UUID invoiceId) { this.invoiceId = invoiceId; }
    public void setKind(Kind kind) { this.kind = kind; }
    public void setMetric(String metric) { this.metric = metric; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
    public void setUnitPriceCents(long unitPriceCents) { this.unitPriceCents = unitPriceCents; }
    public void setAmountCents(long amountCents) { this.amountCents = amountCents; }
}
