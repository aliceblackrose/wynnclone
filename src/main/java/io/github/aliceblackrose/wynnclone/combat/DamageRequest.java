package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record DamageRequest(
        DamageType type,
        double baseDamage,
        double additiveDamage,
        double multiplier,
        double criticalChanceBonus,
        double criticalDamageMultiplierBonus) {

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
        if (!Double.isFinite(criticalChanceBonus)) {
            throw new IllegalArgumentException("Critical chance bonus must be finite");
        }
        if (!Double.isFinite(criticalDamageMultiplierBonus)) {
            throw new IllegalArgumentException("Critical damage multiplier bonus must be finite");
        }
    }

    public static DamageRequest physical(double baseDamage) {
        return new DamageRequest(DamageType.PHYSICAL, baseDamage, 0.0, 1.0, 0.0, 0.0);
    }

    public static DamageRequest magic(double baseDamage) {
        return new DamageRequest(DamageType.MAGIC, baseDamage, 0.0, 1.0, 0.0, 0.0);
    }

    public DamageRequest withCriticalChanceBonus(double bonus) {
        return new DamageRequest(
                type,
                baseDamage,
                additiveDamage,
                multiplier,
                bonus,
                criticalDamageMultiplierBonus);
    }
}
