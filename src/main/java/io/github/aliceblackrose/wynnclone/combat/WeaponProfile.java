package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record WeaponProfile(double baseDamage, AttackSpeed attackSpeed) {
    public WeaponProfile {
        if (!Double.isFinite(baseDamage) || baseDamage <= 0.0) {
            throw new IllegalArgumentException("Weapon damage must be finite and positive");
        }
        Objects.requireNonNull(attackSpeed, "attackSpeed");
    }
}
