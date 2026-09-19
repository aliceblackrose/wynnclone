package io.github.aliceblackrose.wynnclone.combat.status;

import java.util.Objects;
import java.util.UUID;

public record ActiveStatusEffect(
        StatusEffectType type,
        UUID sourceId,
        double strength,
        long expiresAtMillis,
        long nextTickAtMillis,
        long tickIntervalMillis) {

    public ActiveStatusEffect {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(sourceId, "sourceId");

        if (!Double.isFinite(strength) || strength <= 0.0) {
            throw new IllegalArgumentException("Status effect strength must be finite and positive");
        }
        if (tickIntervalMillis <= 0L) {
            throw new IllegalArgumentException("Status effect tick interval must be positive");
        }
    }

    public ActiveStatusEffect withNextTick(long nextTickAtMillis) {
        return new ActiveStatusEffect(
                type,
                sourceId,
                strength,
                expiresAtMillis,
                nextTickAtMillis,
                tickIntervalMillis);
    }
}
