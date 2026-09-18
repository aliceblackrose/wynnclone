package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class AttackCooldownTest {
    @Test
    void attackBecomesReadyAtDeadline() {
        var cooldown = new AttackCooldown();

        cooldown.trigger(1000L, 650L);

        assertFalse(cooldown.ready(1649L));
        assertEquals(1L, cooldown.remainingMillis(1649L));
        assertTrue(cooldown.ready(1650L));
    }

    @Test
    void resetMakesAttackImmediatelyReady() {
        var cooldown = new AttackCooldown();

        cooldown.trigger(1000L, 1000L);
        cooldown.reset();

        assertTrue(cooldown.ready(1000L));
    }
}
