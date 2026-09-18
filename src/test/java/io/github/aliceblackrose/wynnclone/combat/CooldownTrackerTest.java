package io.github.aliceblackrose.wynnclone.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CooldownTrackerTest {
    @Test
    void cooldownExpiresAtStoredDeadline() {
        var tracker = new CooldownTracker();
        var ability = new AbilityId("test.ability");

        tracker.start(ability, 1000L, 500L);

        assertFalse(tracker.ready(ability, 1200L));
        assertEquals(300L, tracker.remainingMillis(ability, 1200L));
        assertTrue(tracker.ready(ability, 1500L));
        assertEquals(0L, tracker.remainingMillis(ability, 1500L));
    }

    @Test
    void cooldownsAreIndependentPerAbility() {
        var tracker = new CooldownTracker();
        var first = new AbilityId("test.first");
        var second = new AbilityId("test.second");

        tracker.start(first, 0L, 1000L);

        assertFalse(tracker.ready(first, 500L));
        assertTrue(tracker.ready(second, 500L));
    }
}
