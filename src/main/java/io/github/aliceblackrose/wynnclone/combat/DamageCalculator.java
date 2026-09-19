package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

public final class DamageCalculator {
    public static final double MAX_DAMAGE = 1_000_000.0;
    private static final double DEFENSE_SCALE = 100.0;

    private final DoubleSupplier criticalRoll;

    public DamageCalculator() {
        this(() -> ThreadLocalRandom.current().nextDouble());
    }

    public DamageCalculator(DoubleSupplier criticalRoll) {
        this.criticalRoll = Objects.requireNonNull(criticalRoll, "criticalRoll");
    }

    public DamageResult calculate(
            DamageRequest request,
            CombatStats attackerStats,
            CombatStats defenderStats) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(attackerStats, "attackerStats");
        Objects.requireNonNull(defenderStats, "defenderStats");

        double summedDamage = request.baseDamage() + request.additiveDamage();
        if (!Double.isFinite(summedDamage)) {
            throw new IllegalArgumentException("Calculated raw damage is not finite");
        }
        double rawDamage = Math.clamp(summedDamage, 0.0, MAX_DAMAGE);

        double scaled = rawDamage * request.multiplier() * attackerStats.damageMultiplier(request.type());
        if (!Double.isFinite(scaled)) {
            throw new IllegalArgumentException("Calculated scaled damage is not finite");
        }
        double scaledDamage = Math.min(scaled, MAX_DAMAGE);

        double criticalChance = Math.clamp(
                attackerStats.criticalChance() + request.criticalChanceBonus(),
                0.0,
                1.0);
        boolean critical = criticalChance > 0.0 && nextCriticalRoll() < criticalChance;

        double preMitigationDamage = scaledDamage;
        if (critical) {
            double criticalMultiplier = Math.max(
                    1.0,
                    attackerStats.criticalDamageMultiplier() + request.criticalDamageMultiplierBonus());
            preMitigationDamage *= criticalMultiplier;
            if (!Double.isFinite(preMitigationDamage)) {
                throw new IllegalArgumentException("Calculated critical damage is not finite");
            }
            preMitigationDamage = Math.min(preMitigationDamage, MAX_DAMAGE);
        }

        double mitigationMultiplier = DEFENSE_SCALE / (DEFENSE_SCALE + defenderStats.defense());
        double finalDamage = preMitigationDamage * mitigationMultiplier;
        if (!Double.isFinite(finalDamage)) {
            throw new IllegalArgumentException("Calculated final damage is not finite");
        }

        return new DamageResult(
                request.type(),
                rawDamage,
                scaledDamage,
                critical,
                Math.min(finalDamage, MAX_DAMAGE));
    }

    private double nextCriticalRoll() {
        double roll = criticalRoll.getAsDouble();
        if (!Double.isFinite(roll) || roll < 0.0 || roll >= 1.0) {
            throw new IllegalStateException("Critical roll must be finite and in [0, 1)");
        }
        return roll;
    }
}
