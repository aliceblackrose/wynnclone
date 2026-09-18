package io.github.aliceblackrose.wynnclone.combat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class CombatDamageService {
    public void damage(Player attacker, LivingEntity target, double amount) {
        if (!Double.isFinite(amount) || amount <= 0.0) {
            throw new IllegalArgumentException("Damage must be finite and positive");
        }
        if (target.isValid() && !target.isDead()) {
            target.damage(amount, attacker);
        }
    }

    public void knockAway(Player source, LivingEntity target, double horizontalStrength, double verticalStrength) {
        if (!Double.isFinite(horizontalStrength) || horizontalStrength < 0.0) {
            throw new IllegalArgumentException("Horizontal knockback must be finite and non-negative");
        }
        if (!Double.isFinite(verticalStrength)) {
            throw new IllegalArgumentException("Vertical knockback must be finite");
        }

        Vector direction = target.getLocation().toVector().subtract(source.getLocation().toVector()).setY(0.0);
        if (direction.lengthSquared() < 1.0e-9) {
            direction = source.getLocation().getDirection().setY(0.0);
        }
        if (direction.lengthSquared() < 1.0e-9) {
            return;
        }

        Vector velocity = direction.normalize().multiply(horizontalStrength).setY(verticalStrength);
        target.setVelocity(target.getVelocity().add(velocity));
    }
}
