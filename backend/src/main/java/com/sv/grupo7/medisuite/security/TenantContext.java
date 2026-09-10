package com.sv.grupo7.medisuite.security;

public final class TenantContext {
    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();
    private TenantContext() {}
    public static void set(Long tenantId) { CURRENT.set(tenantId); }
    public static Long currentTenantId() {
        Long id = CURRENT.get();
        if (id == null) throw new IllegalStateException("Tenant no resuelto");
        return id;
    }
    public static void clear() { CURRENT.remove(); }
}
