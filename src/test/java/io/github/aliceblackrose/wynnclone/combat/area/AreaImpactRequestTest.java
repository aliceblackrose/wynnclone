package io.github.aliceblackrose.wynnclone.combat.area;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class AreaImpactRequestTest {
    @Test
    void storesValidatedAreaCombatParameters() {
        var request = new AreaImpactRequest(
                2.5,
                1.5,
                DamageRequest.physical(6.0),
                Optional.empty(),
                0.7,
                0.2);

        assertEquals(2.5, request.radius());
        assertEquals(1.5, request.verticalRadius());
        assertEquals(0.7, request.horizontalKnockback());
        assertEquals(0.2, request.verticalKnockback());
    }

    @Test
    void rejectsInvalidAreaValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaImpactRequest(
                        0.0,
                        1.0,
                        DamageRequest.physical(1.0),
                        Optional.empty(),
                        0.0,
                        0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaImpactRequest(
                        2.0,
                        -1.0,
                        DamageRequest.physical(1.0),
                        Optional.empty(),
                        0.0,
                        0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaImpactRequest(
                        2.0,
                        1.0,
                        DamageRequest.physical(1.0),
                        Optional.empty(),
                        -0.1,
                        0.0));
    }
}
