-- Plan for Tenant A
INSERT INTO plans (id, tenant_id, name, base_fee_cents, currency)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           '11111111-1111-1111-1111-111111111111',
           'Starter',
           5000,
           'USD'
       )
    ON CONFLICT DO NOTHING;

-- Metered metric: api_calls @ 2 cents each
INSERT INTO plan_metered_prices (id, tenant_id, plan_id, metric, unit_price_cents)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           '11111111-1111-1111-1111-111111111111',
           '33333333-3333-3333-3333-333333333333',
           'api_calls',
           2
       )
    ON CONFLICT DO NOTHING;

-- Link existing subscription to plan
UPDATE subscriptions
SET plan_id = '33333333-3333-3333-3333-333333333333'
WHERE id = '22222222-2222-2222-2222-222222222222';