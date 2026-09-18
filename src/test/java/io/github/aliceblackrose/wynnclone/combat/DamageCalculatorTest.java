package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class DamageCalculatorTest {
    private final DamageCalculator calculator = new DamageCalculator();

    @Test
    void appliesAdditiveMultiplierAndDefenseInOrder() {
        var request = new DamageRequest(
                DamageType.PHYSICAL,
                10.0,
                5.0,
                2.0,
                100.0);

        DamageResult result = calculator.calculate(request);

        assertEquals(15.0, result.rawDamage());
        assertEquals(30.0, result.modifiedDamage());
        assertEquals(15.0, result.finalDamage());
    }

    @Test
    void negativeAdditiveDamageCannotProduceNegativeDamage() {
        var request = new DamageRequest(
                DamageType.MAGIC,
                5.0,
                -10.0,
                3.0,
                0.0);

        DamageResult result = calculator.calculate(request);

        assertEquals(0.0, result.rawDamage());
        assertEquals(0.0, result.finalDamage());
    }

    @Test
    void capsExtremeFiniteDamage() {
        var request = new DamageRequest(
                DamageType.PHYSICAL,
                DamageCalculator.MAX_DAMAGE,
                0.0,
                100.0,
                0.0);

        DamageResult result = calculator.calculate(request);

        assertEquals(DamageCalculator.MAX_DAMAGE, result.finalDamage());
    }

    @Test
    void requestRejectsInvalidNumbers() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageRequest(DamageType.PHYSICAL, Double.NaN, 0.0, 1.0, 0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageRequest(DamageType.PHYSICAL, 1.0, 0.0, -1.0, 0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageRequest(DamageType.PHYSICAL, 1.0, 0.0, 1.0, -1.0));
    }
}
