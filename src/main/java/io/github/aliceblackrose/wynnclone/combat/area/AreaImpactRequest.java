package io.github.aliceblackrose.wynnclone.combat.area;

import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectSpec;
import java.util.Objects;
import java.util.Optional;

public record AreaImpactRequest(
        double radius,
        double verticalRadius,
        DamageRequest damage,
        Optional<StatusEffectSpec> onHitEffect,
        double horizontalKnockback,
        double verticalKnockback) {

    public AreaImpactRequest {
        if (!Double.isFinite(radius) || radius <= 0.0) {
            throw new IllegalArgumentException("Area radius must be finite and positive");
        }
        if (!Double.isFinite(verticalRadius) || verticalRadius < 0.0) {
            throw new IllegalArgumentException("Area vertical radius must be finite and non-negative");
        }
        Objects.requireNonNull(damage, "damage");
        Objects.requireNonNull(onHitEffect, "onHitEffect");
        if (!Double.isFinite(horizontalKnockback) || horizontalKnockback < 0.0) {
            throw new IllegalArgumentException("Horizontal knockback must be finite and non-negative");
        }
        if (!Double.isFinite(verticalKnockback)) {
            throw new IllegalArgumentException("Vertical knockback must be finite");
        }
    }
}
