package io.github.aliceblackrose.wynnclone.combat.projectile;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

final class ActiveProjectile {
    private final UUID ownerId;
    private final UUID worldId;
    private final Vec3 direction;
    private final ProjectileSpec spec;
    private final Set<UUID> hitEntityIds = new HashSet<>();
    private Vec3 position;
    private double traveledDistance;

    ActiveProjectile(
            UUID ownerId,
            UUID worldId,
            Vec3 position,
            Vec3 direction,
            ProjectileSpec spec) {
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        this.worldId = Objects.requireNonNull(worldId, "worldId");
        this.position = Objects.requireNonNull(position, "position");
        this.direction = Objects.requireNonNull(direction, "direction").normalize();
        this.spec = Objects.requireNonNull(spec, "spec");
    }

    UUID ownerId() {
        return ownerId;
    }

    UUID worldId() {
        return worldId;
    }

    Vec3 position() {
        return position;
    }

    Vec3 direction() {
        return direction;
    }

    ProjectileSpec spec() {
        return spec;
    }

    double remainingDistance() {
        return Math.max(0.0, spec.maxDistance() - traveledDistance);
    }

    Vec3 advance(double distance) {
        if (!Double.isFinite(distance) || distance < 0.0) {
            throw new IllegalArgumentException("Projectile advance distance must be finite and non-negative");
        }
        if (distance > remainingDistance() + 1.0e-9) {
            throw new IllegalArgumentException("Projectile cannot advance past its max distance");
        }

        position = position.add(direction.multiply(distance));
        traveledDistance += distance;
        return position;
    }

    boolean hasHit(UUID entityId) {
        return hitEntityIds.contains(Objects.requireNonNull(entityId, "entityId"));
    }

    void recordHit(UUID entityId) {
        Objects.requireNonNull(entityId, "entityId");
        if (!hitEntityIds.add(entityId)) {
            throw new IllegalStateException("Projectile cannot record the same entity hit twice");
        }
    }

    boolean hitLimitReached() {
        return hitEntityIds.size() >= spec.maxEntityHits();
    }

    boolean exhausted() {
        return remainingDistance() <= 1.0e-9;
    }
}
