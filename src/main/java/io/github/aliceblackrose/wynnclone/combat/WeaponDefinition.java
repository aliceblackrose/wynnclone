package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record WeaponDefinition(
        String id,
        double baseDamage,
        AttackSpeed attackSpeed,
        double criticalChanceBonus) {

    public WeaponDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(attackSpeed, "attackSpeed");

        id = id.trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Weapon id must not be blank");
        }
        if (!Double.isFinite(baseDamage) || baseDamage <= 0.0) {
            throw new IllegalArgumentException("Weapon damage must be finite and positive");
        }
        if (!Double.isFinite(criticalChanceBonus) || criticalChanceBonus < 0.0 || criticalChanceBonus > 1.0) {
            throw new IllegalArgumentException("Critical chance bonus must be finite and between 0 and 1");
        }
    }
}
