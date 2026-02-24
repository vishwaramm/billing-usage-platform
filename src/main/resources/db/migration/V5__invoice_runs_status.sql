ALTER TABLE invoice_runs
    ADD COLUMN IF NOT EXISTS status text NOT NULL DEFAULT 'STARTED';

ALTER TABLE invoice_runs
    ADD COLUMN IF NOT EXISTS completed_at timestamptz NULL;

CREATE INDEX IF NOT EXISTS idx_invoice_runs_tenant_period
    ON invoice_runs(tenant_id, period_start, period_end);

