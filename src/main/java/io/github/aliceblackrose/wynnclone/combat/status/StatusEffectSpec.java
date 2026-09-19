package io.github.aliceblackrose.wynnclone.combat.status;

import java.util.Objects;

public record StatusEffectSpec(
        StatusEffectType type,
        double strength,
        long durationMillis,
        long tickIntervalMillis,
        StatusStackPolicy stackPolicy) {

    public StatusEffectSpec {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(stackPolicy, "stackPolicy");

        if (!Double.isFinite(strength) || strength <= 0.0) {
            throw new IllegalArgumentException("Status effect strength must be finite and positive");
        }
        if (durationMillis <= 0L) {
            throw new IllegalArgumentException("Status effect duration must be positive");
        }
        if (tickIntervalMillis <= 0L) {
            throw new IllegalArgumentException("Status effect tick interval must be positive");
        }
    }
}
