package io.github.aliceblackrose.wynnclone.combat.telegraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import io.github.aliceblackrose.wynnclone.combat.area.AreaImpactRequest;
import java.util.Optional;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

final class AreaTelegraphSpecTest {
    private static final AreaImpactRequest IMPACT = new AreaImpactRequest(
            2.0,
            1.0,
            DamageRequest.physical(5.0),
            Optional.empty(),
            0.0,
            0.0);

    @Test
    void storesDelayAndImpactDefinition() {
        var spec = new AreaTelegraphSpec(
                350L,
                Particle.CRIT,
                Particle.SWEEP_ATTACK,
                IMPACT);

        assertEquals(350L, spec.delayMillis());
        assertEquals(IMPACT, spec.impact());
    }

    @Test
    void rejectsNonPositiveDelay() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaTelegraphSpec(
                        0L,
                        Particle.CRIT,
                        Particle.SWEEP_ATTACK,
                        IMPACT));
    }
}
