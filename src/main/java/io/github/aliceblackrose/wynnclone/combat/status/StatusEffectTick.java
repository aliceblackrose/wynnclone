package io.github.aliceblackrose.wynnclone.combat.status;

import java.util.Objects;
import java.util.UUID;

public record StatusEffectTick(
        StatusEffectType type,
        UUID sourceId,
        double strength) {

    public StatusEffectTick {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(sourceId, "sourceId");

        if (!Double.isFinite(strength) || strength <= 0.0) {
            throw new IllegalArgumentException("Status effect tick strength must be finite and positive");
        }
    }
}
