package io.github.aliceblackrose.wynnclone.ability.impl;

import io.github.aliceblackrose.wynnclone.ability.AbilityAction;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class DashAbility implements AbilityAction {
    @Override
    public boolean execute(Player caster) {
        Vector direction = caster.getLocation().getDirection().setY(0.0);
        if (direction.lengthSquared() < 1.0e-9) {
            return false;
        }

        caster.setVelocity(direction.normalize().multiply(1.35).setY(0.25));
        caster.getWorld().spawnParticle(
                Particle.CLOUD,
                caster.getLocation().add(0.0, 0.2, 0.0),
                14,
                0.35,
                0.1,
                0.35,
                0.03);
        return true;
    }
}
