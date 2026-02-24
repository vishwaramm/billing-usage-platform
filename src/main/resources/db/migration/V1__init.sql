CREATE TABLE tenants (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE subscriptions (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    status text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_sub_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE TABLE usage_events (
    id uuid PRIMARY KEY,
    tenant_id uuid NOT NULL,
    subscription_id uuid NOT NULL,
    idempotency_key text NOT NULL,
    metric text NOT NULL,
    quantity bigint NOT NULL,
    occurred_at timestamptz NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_ue_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ue_sub FOREIGN KEY (subscription_id) REFERENCES  subscriptions(id),

    CONSTRAINT uq_ue_idempo UNIQUE (tenant_id, idempotency_key)
);

CREATE INDEX idx_sub_tenant ON subscriptions(tenant_id);
CREATE INDEX idx_ue_tenant_sub_time ON usage_events(tenant_id, subscription_id, occurred_at);