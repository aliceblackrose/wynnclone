package io.github.aliceblackrose.wynnclone.combat.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

final class StatusEffectTrackerTest {
    private static final UUID FIRST_SOURCE = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_SOURCE = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void refreshKeepsStrongestStrengthAndExtendsFromNewApplication() {
        var tracker = new StatusEffectTracker();
        var first = new StatusEffectSpec(
                StatusEffectType.BURN,
                2.0,
                2000L,
                500L,
                StatusStackPolicy.REFRESH);
        var weakerRefresh = new StatusEffectSpec(
                StatusEffectType.BURN,
                1.0,
                3000L,
                500L,
                StatusStackPolicy.REFRESH);

        tracker.apply(FIRST_SOURCE, first, 100L);
        tracker.apply(SECOND_SOURCE, weakerRefresh, 600L);

        ActiveStatusEffect effect = tracker.effect(StatusEffectType.BURN).orElseThrow();
        assertEquals(2.0, effect.strength());
        assertEquals(3600L, effect.expiresAtMillis());
        assertEquals(SECOND_SOURCE, effect.sourceId());
    }

    @Test
    void stackIntensityAddsStrengthWithoutCreatingDuplicateTypes() {
        var tracker = new StatusEffectTracker();
        var bleed = new StatusEffectSpec(
                StatusEffectType.BLEED,
                1.5,
                1500L,
                500L,
                StatusStackPolicy.STACK_INTENSITY);

        tracker.apply(FIRST_SOURCE, bleed, 0L);
        tracker.apply(SECOND_SOURCE, bleed, 100L);

        ActiveStatusEffect effect = tracker.effect(StatusEffectType.BLEED).orElseThrow();
        assertEquals(3.0, effect.strength());
        assertEquals(1, tracker.activeTypes().size());
    }

    @Test
    void emitsDueTickThenExpires() {
        var tracker = new StatusEffectTracker();
        tracker.apply(
                FIRST_SOURCE,
                new StatusEffectSpec(
                        StatusEffectType.BURN,
                        2.0,
                        1000L,
                        500L,
                        StatusStackPolicy.REPLACE),
                0L);

        assertTrue(tracker.pollDueTicks(499L).isEmpty());

        var firstTick = tracker.pollDueTicks(500L);
        assertEquals(1, firstTick.size());
        assertEquals(StatusEffectType.BURN, firstTick.getFirst().type());
        assertEquals(2.0, firstTick.getFirst().strength());

        var finalTick = tracker.pollDueTicks(1000L);
        assertEquals(1, finalTick.size());
        assertTrue(tracker.isEmpty());
    }

    @Test
    void expiredEffectIsRemovedWithoutLateDamageBurst() {
        var tracker = new StatusEffectTracker();
        tracker.apply(
                FIRST_SOURCE,
                new StatusEffectSpec(
                        StatusEffectType.BURN,
                        2.0,
                        1000L,
                        500L,
                        StatusStackPolicy.REPLACE),
                0L);

        assertTrue(tracker.pollDueTicks(1500L).isEmpty());
        assertTrue(tracker.isEmpty());
    }
}
