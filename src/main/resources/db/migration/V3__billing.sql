CREATE TABLE plans (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    name text,
    base_fee_cents bigint NOT NULL,
    currency text NOT NULL DEFAULT 'USD',
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_plan_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_plans_tenant ON plans(tenant_id);

CREATE TABLE plan_metered_prices (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    plan_id uuid NOT NULL,
    metric text NOT NULL,
    unit_price_cents bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_pmp_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_pmp_plan FOREIGN KEY  (plan_id)  REFERENCES plans(id),
    CONSTRAINT uq_plan_metric UNIQUE (tenant_id, plan_id, metric)
);

CREATE INDEX idx_pmp_tenant_plan ON plan_metered_prices(tenant_id, plan_id);

ALTER TABLE subscriptions
    ADD COLUMN IF NOT EXISTS plan_id uuid;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_sub_plan') THEN
ALTER TABLE subscriptions
    ADD CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES plans(id);
END IF;
END $$;

CREATE INDEX idx_sub_tenant_plan ON subscriptions(tenant_id, plan_id);

CREATE TABLE invoices (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    subscription_id uuid NOT NULL,
    period_start date NOT NULL,
    period_end date NOT NULL,
    status text NOT NULL,
    currency text NOT NULL DEFAULT 'USD',
    total_cents bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_inv_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_inv_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),

    CONSTRAINT uq_uq_inv_tenant_sub_period UNIQUE (tenant_id, subscription_id, period_start, period_end)
);

CREATE INDEX idx_inv_tenant_period ON invoices(tenant_id, period_start, period_end);

CREATE TABLE invoice_line_items (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    invoice_id uuid NOT NULL,
    kind text NOT NULL,
    metric text NULL,
    description text NOT NULL,
    quantity bigint NOT NULL,
    unit_price_cents bigint NOT NULL,
    amount_cents bigint NOT NULL,

    CONSTRAINT fk_invli_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_invli_inv FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE INDEX idx_invli_tenant_inv ON invoice_line_items(tenant_id, invoice_id);

ALTER TABLE usage_events
    ADD COLUMN billed_invoice_id uuid NULL;

ALTER TABLE usage_events
    ADD CONSTRAINT fk_ue_inv FOREIGN KEY (billed_invoice_id) REFERENCES invoices(id);

CREATE INDEX idx_ue_unbilled ON usage_events(tenant_id, subscription_id, occurred_at)
    WHERE billed_invoice_id IS NULL;

CREATE TABLE invoice_runs (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    period_start date NOT NULL,
    period_end date NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_invr_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uq_run_tenant_period UNIQUE (tenant_id, period_start, period_end)
);




