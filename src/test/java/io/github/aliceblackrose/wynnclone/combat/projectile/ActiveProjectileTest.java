package io.github.aliceblackrose.wynnclone.combat.projectile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

final class ActiveProjectileTest {
    private static final ProjectileSpec PIERCING = new ProjectileSpec(
            1.0,
            10.0,
            0.25,
            DamageRequest.magic(4.0),
            Particle.END_ROD,
            Particle.CRIT,
            Optional.empty(),
            2,
            0.0);

    @Test
    void tracksDistinctHitsUntilLimit() {
        var projectile = projectile();
        UUID first = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID second = UUID.fromString("00000000-0000-0000-0000-000000000002");

        projectile.recordHit(first);

        assertTrue(projectile.hasHit(first));
        assertFalse(projectile.hitLimitReached());

        projectile.recordHit(second);

        assertTrue(projectile.hitLimitReached());
    }

    @Test
    void duplicateEntityHitIsRejected() {
        var projectile = projectile();
        UUID target = UUID.fromString("00000000-0000-0000-0000-000000000001");

        projectile.recordHit(target);

        assertThrows(IllegalStateException.class, () -> projectile.recordHit(target));
    }

    private static ActiveProjectile projectile() {
        return new ActiveProjectile(
                UUID.fromString("00000000-0000-0000-0000-000000000010"),
                UUID.fromString("00000000-0000-0000-0000-000000000020"),
                new Vec3(0.0, 0.0, 0.0),
                new Vec3(1.0, 0.0, 0.0),
                PIERCING);
    }
}
