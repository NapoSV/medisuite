package com.sv.grupo7.medisuite.security;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtBlacklist {
    private final Set<String> revoked = ConcurrentHashMap.newKeySet();
    public void revoke(String token) { revoked.add(token); }
    public boolean isRevoked(String token) { return revoked.contains(token); }
}
