package com.vishwaram.bilingusageplatform.infra;

import java.util.UUID;

public final class TenantContext {
    private static ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    public TenantContext() {
    }

    public static void set(UUID tenantId) {
        CURRENT.set(tenantId);
    }

    public static UUID getRequired() {
        UUID tenantId = CURRENT.get();

        if (tenantId == null) {
            throw new IllegalArgumentException("TenantContext missing");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
