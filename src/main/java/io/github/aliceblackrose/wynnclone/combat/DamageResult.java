package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record DamageResult(
        DamageType type,
        double rawDamage,
        double modifiedDamage,
        double finalDamage) {

    public DamageResult {
        Objects.requireNonNull(type, "type");
        validate(rawDamage, "Raw damage");
        validate(modifiedDamage, "Modified damage");
        validate(finalDamage, "Final damage");
    }

    private static void validate(double value, String label) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(label + " must be finite and non-negative");
        }
    }
}
