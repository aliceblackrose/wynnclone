package io.github.aliceblackrose.wynnclone.combat.projectile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import java.util.Optional;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

final class ProjectileSpecTest {
    @Test
    void simpleProjectileUsesSingleHitAndNoExplosion() {
        ProjectileSpec spec = ProjectileSpec.simple(
                1.0,
                10.0,
                0.25,
                DamageRequest.magic(5.0),
                Particle.END_ROD,
                Particle.CRIT);

        assertEquals(1, spec.maxEntityHits());
        assertEquals(0.0, spec.explosionRadius());
    }

    @Test
    void piercingProjectileAllowsMultipleEntityHits() {
        var spec = new ProjectileSpec(
                1.2,
                20.0,
                0.4,
                DamageRequest.magic(7.0),
                Particle.END_ROD,
                Particle.CRIT,
                Optional.empty(),
                3,
                0.0);

        assertEquals(3, spec.maxEntityHits());
    }

    @Test
    void explosiveProjectileMustStopOnFirstEntity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectileSpec(
                        1.0,
                        12.0,
                        0.4,
                        DamageRequest.magic(8.0),
                        Particle.END_ROD,
                        Particle.CRIT,
                        Optional.empty(),
                        2,
                        2.5));
    }

    @Test
    void rejectsInvalidHitAndExplosionValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectileSpec(
                        1.0,
                        12.0,
                        0.4,
                        DamageRequest.magic(8.0),
                        Particle.END_ROD,
                        Particle.CRIT,
                        Optional.empty(),
                        0,
                        0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectileSpec(
                        1.0,
                        12.0,
                        0.4,
                        DamageRequest.magic(8.0),
                        Particle.END_ROD,
                        Particle.CRIT,
                        Optional.empty(),
                        1,
                        -1.0));
    }
}
