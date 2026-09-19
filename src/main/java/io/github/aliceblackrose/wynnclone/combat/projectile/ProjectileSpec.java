package io.github.aliceblackrose.wynnclone.combat.projectile;

import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectSpec;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Particle;

public record ProjectileSpec(
        double speedPerTick,
        double maxDistance,
        double collisionRadius,
        DamageRequest damage,
        Particle trailParticle,
        Particle impactParticle,
        Optional<StatusEffectSpec> onHitEffect,
        int maxEntityHits,
        double explosionRadius) {

    public ProjectileSpec {
        if (!Double.isFinite(speedPerTick) || speedPerTick <= 0.0) {
            throw new IllegalArgumentException("Projectile speed must be finite and positive");
        }
        if (!Double.isFinite(maxDistance) || maxDistance <= 0.0) {
            throw new IllegalArgumentException("Projectile max distance must be finite and positive");
        }
        if (!Double.isFinite(collisionRadius) || collisionRadius < 0.0) {
            throw new IllegalArgumentException("Projectile collision radius must be finite and non-negative");
        }
        if (maxEntityHits < 1) {
            throw new IllegalArgumentException("Projectile max entity hits must be positive");
        }
        if (!Double.isFinite(explosionRadius) || explosionRadius < 0.0) {
            throw new IllegalArgumentException("Projectile explosion radius must be finite and non-negative");
        }
        if (explosionRadius > 0.0 && maxEntityHits != 1) {
            throw new IllegalArgumentException("Explosive projectiles must stop on their first entity hit");
        }

        Objects.requireNonNull(damage, "damage");
        Objects.requireNonNull(trailParticle, "trailParticle");
        Objects.requireNonNull(impactParticle, "impactParticle");
        Objects.requireNonNull(onHitEffect, "onHitEffect");
    }

    public static ProjectileSpec simple(
            double speedPerTick,
            double maxDistance,
            double collisionRadius,
            DamageRequest damage,
            Particle trailParticle,
            Particle impactParticle) {
        return new ProjectileSpec(
                speedPerTick,
                maxDistance,
                collisionRadius,
                damage,
                trailParticle,
                impactParticle,
                Optional.empty(),
                1,
                0.0);
    }
}
