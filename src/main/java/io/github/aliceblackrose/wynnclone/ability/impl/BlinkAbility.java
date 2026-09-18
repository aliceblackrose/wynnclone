package io.github.aliceblackrose.wynnclone.ability.impl;

import io.github.aliceblackrose.wynnclone.ability.AbilityAction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class BlinkAbility implements AbilityAction {
    private static final double RANGE = 7.0;
    private static final double STEP = 0.5;

    @Override
    public boolean execute(Player caster) {
        Location origin = caster.getLocation();
        Vector direction = origin.getDirection();
        if (direction.lengthSquared() < 1.0e-9) {
            return false;
        }
        direction.normalize();

        Location lastSafe = null;
        for (double distance = STEP; distance <= RANGE; distance += STEP) {
            Location candidate = origin.clone().add(direction.clone().multiply(distance));
            if (!isPassable(candidate)) {
                break;
            }
            lastSafe = candidate;
        }

        if (lastSafe == null) {
            return false;
        }

        caster.getWorld().spawnParticle(Particle.PORTAL, origin.clone().add(0.0, 1.0, 0.0), 24, 0.3, 0.6, 0.3, 0.2);
        boolean teleported = caster.teleport(lastSafe);
        if (teleported) {
            caster.getWorld().spawnParticle(
                    Particle.PORTAL,
                    caster.getLocation().add(0.0, 1.0, 0.0),
                    24,
                    0.3,
                    0.6,
                    0.3,
                    0.2);
        }
        return teleported;
    }

    private static boolean isPassable(Location location) {
        return location.getBlock().isPassable()
                && location.clone().add(0.0, 1.0, 0.0).getBlock().isPassable();
    }
}
