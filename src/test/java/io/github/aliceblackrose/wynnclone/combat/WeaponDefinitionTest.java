package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class WeaponDefinitionTest {
    @Test
    void storesStableCombatDefinition() {
        var weapon = new WeaponDefinition(
                "test:blade",
                12.0,
                AttackSpeed.FAST,
                0.10);

        assertEquals("test:blade", weapon.id());
        assertEquals(12.0, weapon.baseDamage());
        assertEquals(AttackSpeed.FAST, weapon.attackSpeed());
        assertEquals(0.10, weapon.criticalChanceBonus());
    }

    @Test
    void rejectsInvalidDefinition() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeaponDefinition("", 10.0, AttackSpeed.NORMAL, 0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeaponDefinition("test:bad", 0.0, AttackSpeed.NORMAL, 0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeaponDefinition("test:bad", 10.0, AttackSpeed.NORMAL, 1.1));
    }
}
