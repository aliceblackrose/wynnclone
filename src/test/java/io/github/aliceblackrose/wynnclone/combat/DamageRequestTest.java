package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class DamageRequestTest {
    @Test
    void criticalBonusCopyPreservesOtherDamageFields() {
        DamageRequest original = new DamageRequest(
                DamageType.PHYSICAL,
                12.0,
                3.0,
                1.25,
                0.0,
                0.20,
                true);

        DamageRequest modified = original.withCriticalChanceBonus(0.15);

        assertEquals(original.type(), modified.type());
        assertEquals(original.baseDamage(), modified.baseDamage());
        assertEquals(original.additiveDamage(), modified.additiveDamage());
        assertEquals(original.multiplier(), modified.multiplier());
        assertEquals(0.15, modified.criticalChanceBonus());
        assertEquals(original.criticalDamageMultiplierBonus(), modified.criticalDamageMultiplierBonus());
        assertTrue(modified.criticalEligible());
    }

    @Test
    void withoutCriticalsPreservesDamageAndDisablesCritEligibility() {
        DamageRequest request = DamageRequest.magic(7.0).withCriticalChanceBonus(0.50);

        DamageRequest nonCritical = request.withoutCriticals();

        assertEquals(request.type(), nonCritical.type());
        assertEquals(request.baseDamage(), nonCritical.baseDamage());
        assertEquals(request.criticalChanceBonus(), nonCritical.criticalChanceBonus());
        assertFalse(nonCritical.criticalEligible());
    }

    @Test
    void rejectsInvalidNumbers() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageRequest(DamageType.PHYSICAL, Double.NaN, 0.0, 1.0, 0.0, 0.0, true));
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageRequest(DamageType.PHYSICAL, 1.0, 0.0, -1.0, 0.0, 0.0, true));
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageRequest(
                        DamageType.PHYSICAL,
                        1.0,
                        0.0,
                        1.0,
                        Double.POSITIVE_INFINITY,
                        0.0,
                        true));
    }
}
