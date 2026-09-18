package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class CombatStatsTest {
    @Test
    void defaultsAreUsableCombatValues() {
        CombatStats stats = CombatStats.defaults();

        assertEquals(1.0, stats.physicalDamageMultiplier());
        assertEquals(1.0, stats.magicDamageMultiplier());
        assertEquals(0.05, stats.criticalChance());
        assertEquals(1.5, stats.criticalDamageMultiplier());
        assertEquals(0.0, stats.defense());
    }

    @Test
    void selectsDamageMultiplierByDamageType() {
        var stats = new CombatStats(1.25, 1.75, 0.0, 1.5, 0.0);

        assertEquals(1.25, stats.damageMultiplier(DamageType.PHYSICAL));
        assertEquals(1.75, stats.damageMultiplier(DamageType.MAGIC));
    }

    @Test
    void rejectsInvalidStats() {
        assertThrows(IllegalArgumentException.class, () -> new CombatStats(-1.0, 1.0, 0.0, 1.5, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new CombatStats(1.0, 1.0, 1.1, 1.5, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new CombatStats(1.0, 1.0, 0.1, 0.9, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new CombatStats(1.0, 1.0, 0.1, 1.5, -1.0));
    }
}
