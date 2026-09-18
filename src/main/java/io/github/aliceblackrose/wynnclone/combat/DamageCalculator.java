package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public final class DamageCalculator {
    public static final double MAX_DAMAGE = 1_000_000.0;
    private static final double DEFENSE_SCALE = 100.0;

    public DamageResult calculate(DamageRequest request) {
        Objects.requireNonNull(request, "request");

        double summedDamage = request.baseDamage() + request.additiveDamage();
        if (!Double.isFinite(summedDamage)) {
            throw new IllegalArgumentException("Calculated raw damage is not finite");
        }
        double rawDamage = Math.clamp(summedDamage, 0.0, MAX_DAMAGE);

        double scaledDamage = rawDamage * request.multiplier();
        if (!Double.isFinite(scaledDamage)) {
            throw new IllegalArgumentException("Calculated modified damage is not finite");
        }
        double modifiedDamage = Math.min(scaledDamage, MAX_DAMAGE);

        double mitigationMultiplier = DEFENSE_SCALE / (DEFENSE_SCALE + request.defense());
        double finalDamage = modifiedDamage * mitigationMultiplier;
        if (!Double.isFinite(finalDamage)) {
            throw new IllegalArgumentException("Calculated final damage is not finite");
        }

        return new DamageResult(
                request.type(),
                rawDamage,
                modifiedDamage,
                Math.min(finalDamage, MAX_DAMAGE));
    }
}
