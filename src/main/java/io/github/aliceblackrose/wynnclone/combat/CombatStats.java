package io.github.aliceblackrose.wynnclone.combat;

public record CombatStats(
        double physicalDamageMultiplier,
        double magicDamageMultiplier,
        double criticalChance,
        double criticalDamageMultiplier,
        double defense) {

    public CombatStats {
        validateNonNegativeFinite(physicalDamageMultiplier, "Physical damage multiplier");
        validateNonNegativeFinite(magicDamageMultiplier, "Magic damage multiplier");
        validateNonNegativeFinite(defense, "Defense");

        if (!Double.isFinite(criticalChance) || criticalChance < 0.0 || criticalChance > 1.0) {
            throw new IllegalArgumentException("Critical chance must be finite and between 0 and 1");
        }
        if (!Double.isFinite(criticalDamageMultiplier) || criticalDamageMultiplier < 1.0) {
            throw new IllegalArgumentException("Critical damage multiplier must be finite and at least 1");
        }
    }

    public static CombatStats defaults() {
        return new CombatStats(1.0, 1.0, 0.05, 1.5, 0.0);
    }

    public double damageMultiplier(DamageType type) {
        return switch (type) {
            case PHYSICAL -> physicalDamageMultiplier;
            case MAGIC -> magicDamageMultiplier;
        };
    }

    private static void validateNonNegativeFinite(double value, String label) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(label + " must be finite and non-negative");
        }
    }
}
