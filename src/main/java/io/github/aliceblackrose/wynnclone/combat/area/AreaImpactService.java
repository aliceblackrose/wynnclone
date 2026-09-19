package io.github.aliceblackrose.wynnclone.combat.area;

import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectService;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class AreaImpactService {
    private final CombatDamageService damage;
    private final StatusEffectService statusEffects;

    public AreaImpactService(CombatDamageService damage, StatusEffectService statusEffects) {
        this.damage = Objects.requireNonNull(damage, "damage");
        this.statusEffects = Objects.requireNonNull(statusEffects, "statusEffects");
    }

    public int apply(Player source, Location center, AreaImpactRequest request) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(request, "request");

        if (center.getWorld() == null) {
            return 0;
        }

        double radiusSquared = request.radius() * request.radius();
        int affected = 0;

        for (var entity : center.getWorld().getNearbyEntities(
                center,
                request.radius(),
                request.verticalRadius(),
                request.radius(),
                candidate -> isCombatTarget(source, candidate))) {
            LivingEntity target = (LivingEntity) entity;
            Location targetLocation = target.getLocation();

            double dx = targetLocation.getX() - center.getX();
            double dz = targetLocation.getZ() - center.getZ();
            if (dx * dx + dz * dz > radiusSquared) {
                continue;
            }

            damage.damage(source, target, request.damage());
            request.onHitEffect().ifPresent(effect -> statusEffects.apply(source, target, effect));

            if (request.horizontalKnockback() > 0.0 || request.verticalKnockback() != 0.0) {
                damage.knockAway(
                        center,
                        target,
                        request.horizontalKnockback(),
                        request.verticalKnockback());
            }
            affected++;
        }

        return affected;
    }

    private static boolean isCombatTarget(Player source, org.bukkit.entity.Entity entity) {
        return entity instanceof LivingEntity
                && entity != source
                && !(entity instanceof ArmorStand)
                && entity.isValid()
                && !entity.isDead();
    }
}
