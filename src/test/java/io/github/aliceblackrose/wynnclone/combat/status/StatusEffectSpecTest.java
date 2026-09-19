package io.github.aliceblackrose.wynnclone.combat.status;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class StatusEffectSpecTest {
    @Test
    void rejectsInvalidDefinitions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StatusEffectSpec(
                        StatusEffectType.BURN,
                        0.0,
                        1000L,
                        500L,
                        StatusStackPolicy.REFRESH));
        assertThrows(
                IllegalArgumentException.class,
                () -> new StatusEffectSpec(
                        StatusEffectType.BURN,
                        1.0,
                        0L,
                        500L,
                        StatusStackPolicy.REFRESH));
        assertThrows(
                IllegalArgumentException.class,
                () -> new StatusEffectSpec(
                        StatusEffectType.BURN,
                        1.0,
                        1000L,
                        0L,
                        StatusStackPolicy.REFRESH));
    }
}
