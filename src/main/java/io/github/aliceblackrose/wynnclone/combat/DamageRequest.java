package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record DamageRequest(
        DamageType type,
        double baseDamage,
        double additiveDamage,
        double multiplier,
        double defense) {

    public DamageRequest {
        Objects.requireNonNull(type, "type");
        if (!Double.isFinite(baseDamage) || baseDamage < 0.0) {
            throw new IllegalArgumentException("Base damage must be finite and non-negative");
        }
        if (!Double.isFinite(additiveDamage)) {
            throw new IllegalArgumentException("Additive damage must be finite");
        }
        if (!Double.isFinite(multiplier) || multiplier < 0.0) {
            throw new IllegalArgumentException("Damage multiplier must be finite and non-negative");
        }
        if (!Double.isFinite(defense) || defense < 0.0) {
            throw new IllegalArgumentException("Defense must be finite and non-negative");
        }
    }

    public static DamageRequest physical(double baseDamage) {
        return new DamageRequest(DamageType.PHYSICAL, baseDamage, 0.0, 1.0, 0.0);
    }

    public static DamageRequest magic(double baseDamage) {
        return new DamageRequest(DamageType.MAGIC, baseDamage, 0.0, 1.0, 0.0);
    }
}
