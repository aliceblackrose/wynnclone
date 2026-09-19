package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class DamageCalculatorTest {
    private static final CombatStats NEUTRAL = new CombatStats(1.0, 1.0, 0.0, 1.5, 0.0);

    @Test
    void appliesTypeMultiplierAndDefenseInOrder() {
        var calculator = new DamageCalculator(() -> 0.99);
        var attacker = new CombatStats(1.5, 2.0, 0.0, 1.5, 0.0);
        var defender = new CombatStats(1.0, 1.0, 0.0, 1.5, 100.0);

        DamageResult result = calculator.calculate(DamageRequest.physical(10.0), attacker, defender);

        assertEquals(10.0, result.rawDamage());
        assertEquals(15.0, result.scaledDamage());
        assertFalse(result.critical());
        assertEquals(7.5, result.finalDamage());
    }

    @Test
    void criticalHitUsesInjectedRollAndCriticalMultiplier() {
        var calculator = new DamageCalculator(() -> 0.10);
        var attacker = new CombatStats(1.0, 1.0, 0.25, 2.0, 0.0);

        DamageResult result = calculator.calculate(DamageRequest.physical(10.0), attacker, NEUTRAL);

        assertTrue(result.critical());
        assertEquals(20.0, result.finalDamage());
    }

    @Test
    void rollAboveChanceDoesNotCrit() {
        var calculator = new DamageCalculator(() -> 0.90);
        var attacker = new CombatStats(1.0, 1.0, 0.25, 2.0, 0.0);

        DamageResult result = calculator.calculate(DamageRequest.magic(10.0), attacker, NEUTRAL);

        assertFalse(result.critical());
        assertEquals(10.0, result.finalDamage());
    }

    @Test
    void weaponCriticalBonusAddsToAttackerChance() {
        var calculator = new DamageCalculator(() -> 0.50);
        var attacker = new CombatStats(1.0, 1.0, 0.05, 1.5, 0.0);
        DamageRequest request = DamageRequest.physical(10.0).withCriticalChanceBonus(0.50);

        DamageResult result = calculator.calculate(request, attacker, NEUTRAL);

        assertTrue(result.critical());
        assertEquals(15.0, result.finalDamage());
    }

    @Test
    void nonCriticalDamageNeverSamplesRandomSource() {
        var calculator = new DamageCalculator(() -> {
            throw new AssertionError("Critical RNG should not be sampled");
        });
        var attacker = new CombatStats(1.0, 1.0, 1.0, 5.0, 0.0);

        DamageResult result = calculator.calculate(
                DamageRequest.magic(4.0).withoutCriticals(),
                attacker,
                NEUTRAL);

        assertFalse(result.critical());
        assertEquals(4.0, result.finalDamage());
    }

    @Test
    void negativeAdditiveDamageCannotProduceNegativeDamage() {
        var calculator = new DamageCalculator(() -> 0.99);
        var request = new DamageRequest(
                DamageType.MAGIC,
                5.0,
                -10.0,
                3.0,
                0.0,
                0.0,
                true);

        DamageResult result = calculator.calculate(request, NEUTRAL, NEUTRAL);

        assertEquals(0.0, result.rawDamage());
        assertEquals(0.0, result.finalDamage());
    }

    @Test
    void capsExtremeFiniteDamage() {
        var calculator = new DamageCalculator(() -> 0.99);
        var request = new DamageRequest(
                DamageType.PHYSICAL,
                DamageCalculator.MAX_DAMAGE,
                0.0,
                100.0,
                0.0,
                0.0,
                true);

        DamageResult result = calculator.calculate(request, NEUTRAL, NEUTRAL);

        assertEquals(DamageCalculator.MAX_DAMAGE, result.scaledDamage());
        assertEquals(DamageCalculator.MAX_DAMAGE, result.finalDamage());
    }

    @Test
    void invalidCriticalRollFailsExplicitly() {
        var calculator = new DamageCalculator(() -> 1.0);
        var attacker = new CombatStats(1.0, 1.0, 1.0, 1.5, 0.0);

        assertThrows(
                IllegalStateException.class,
                () -> calculator.calculate(DamageRequest.physical(1.0), attacker, NEUTRAL));
    }
}
