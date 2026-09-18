package io.github.aliceblackrose.wynnclone.combat;

import java.util.List;
import java.util.Optional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class TargetingService {
    public List<LivingEntity> nearby(Player caster, double radius) {
        validateRadius(radius);
        double radiusSquared = radius * radius;
        var origin = caster.getLocation();

        return caster.getWorld()
                .getNearbyEntities(origin, radius, radius, radius, entity -> isCombatTarget(caster, entity))
                .stream()
                .map(LivingEntity.class::cast)
                .filter(entity -> entity.getLocation().distanceSquared(origin) <= radiusSquared)
                .toList();
    }

    public List<LivingEntity> cone(Player caster, double radius, double minimumDot) {
        validateRadius(radius);
        if (!Double.isFinite(minimumDot) || minimumDot < -1.0 || minimumDot > 1.0) {
            throw new IllegalArgumentException("Minimum dot must be between -1 and 1");
        }

        Vector forward = caster.getEyeLocation().getDirection();
        if (forward.lengthSquared() < 1.0e-9) {
            return List.of();
        }
        forward.normalize();

        return nearby(caster, radius).stream()
                .filter(target -> {
                    Vector offset = target.getEyeLocation().toVector().subtract(caster.getEyeLocation().toVector());
                    return offset.lengthSquared() < 1.0e-9 || offset.normalize().dot(forward) >= minimumDot;
                })
                .toList();
    }

    public Optional<LivingEntity> rayTarget(Player caster, double maxDistance, double raySize) {
        validateRadius(maxDistance);
        if (!Double.isFinite(raySize) || raySize < 0.0) {
            throw new IllegalArgumentException("Ray size must be finite and non-negative");
        }

        Vector direction = caster.getEyeLocation().getDirection();
        if (direction.lengthSquared() < 1.0e-9) {
            return Optional.empty();
        }

        RayTraceResult result = caster.getWorld().rayTraceEntities(
                caster.getEyeLocation(),
                direction.normalize(),
                maxDistance,
                raySize,
                entity -> isCombatTarget(caster, entity));

        if (result == null || !(result.getHitEntity() instanceof LivingEntity living)) {
            return Optional.empty();
        }
        return Optional.of(living);
    }

    private static boolean isCombatTarget(Player caster, Entity entity) {
        return entity instanceof LivingEntity
                && entity != caster
                && !(entity instanceof ArmorStand)
                && entity.isValid()
                && !entity.isDead();
    }

    private static void validateRadius(double radius) {
        if (!Double.isFinite(radius) || radius < 0.0) {
            throw new IllegalArgumentException("Radius must be finite and non-negative");
        }
    }
}
