package io.github.aliceblackrose.wynnclone.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CooldownTracker {

    private final Map<AbilityId, Long> expiresAt = new HashMap<>();

    public boolean ready(AbilityId abilityId, long nowMillis) {
        return remainingMillis(abilityId, nowMillis) == 0L;
    }

    public long remainingMillis(AbilityId abilityId, long nowMillis) {
        Objects.requireNonNull(abilityId, "abilityId");
        long expiry = expiresAt.getOrDefault(abilityId, 0L);
        if (expiry <= nowMillis) {
            expiresAt.remove(abilityId);
            return 0L;
        }
        return expiry - nowMillis;
    }

    public void start(AbilityId abilityId, long nowMillis, long durationMillis) {
        Objects.requireNonNull(abilityId, "abilityId");
        if (durationMillis < 0L) {
            throw new IllegalArgumentException("Cooldown duration must be non-negative");
        }

        long expiry;
        try {
            expiry = Math.addExact(nowMillis, durationMillis);
        } catch (ArithmeticException exception) {
            expiry = Long.MAX_VALUE;
        }
        expiresAt.put(abilityId, expiry);
    }

    public void clear() {
        expiresAt.clear();
    }
}
