package io.github.aliceblackrose.wynnclone.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class CombatDamageService {
    private final DamageCalculator calculator;
    private final CombatStateStore states;
    private final Map<DamageKey, Integer> activeApplications = new HashMap<>();

    public CombatDamageService(DamageCalculator calculator, CombatStateStore states) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.states = Objects.requireNonNull(states, "states");
    }

    public DamageResult damage(Player attacker, LivingEntity target, DamageRequest request) {
        Objects.requireNonNull(attacker, "attacker");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(request, "request");

        CombatStats attackerStats = states.state(attacker.getUniqueId()).stats();
        CombatStats defenderStats = target instanceof Player player
                ? states.state(player.getUniqueId()).stats()
                : CombatStats.defaults();

        DamageResult result = calculator.calculate(request, attackerStats, defenderStats);
        if (!target.isValid() || target.isDead() || result.finalDamage() <= 0.0) {
            return result;
        }

        DamageKey key = new DamageKey(attacker.getUniqueId(), target.getUniqueId());
        activeApplications.merge(key, 1, Integer::sum);
        try {
            target.damage(result.finalDamage(), attacker);
        } finally {
            activeApplications.computeIfPresent(key, (ignored, depth) -> depth <= 1 ? null : depth - 1);
        }

        if (result.critical()) {
            target.getWorld().spawnParticle(
                    Particle.CRIT,
                    target.getLocation().add(0.0, 1.0, 0.0),
                    12,
                    0.35,
                    0.45,
                    0.35,
                    0.08);
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.7f, 1.15f);
        }

        return result;
    }

    public boolean isInternal(Player attacker, LivingEntity target) {
        Objects.requireNonNull(attacker, "attacker");
        Objects.requireNonNull(target, "target");
        return activeApplications.containsKey(new DamageKey(attacker.getUniqueId(), target.getUniqueId()));
    }

    public void knockAway(Player source, LivingEntity target, double horizontalStrength, double verticalStrength) {
        Objects.requireNonNull(source, "source");
        knockAway(source.getLocation(), target, horizontalStrength, verticalStrength);
    }

    public void knockAway(Location source, LivingEntity target, double horizontalStrength, double verticalStrength) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");

        if (!Double.isFinite(horizontalStrength) || horizontalStrength < 0.0) {
            throw new IllegalArgumentException("Horizontal knockback must be finite and non-negative");
        }
        if (!Double.isFinite(verticalStrength)) {
            throw new IllegalArgumentException("Vertical knockback must be finite");
        }

        Vector impulse = new Vector(0.0, verticalStrength, 0.0);
        if (horizontalStrength > 0.0) {
            Vector direction = target.getLocation().toVector().subtract(source.toVector()).setY(0.0);
            if (direction.lengthSquared() >= 1.0e-9) {
                impulse.add(direction.normalize().multiply(horizontalStrength));
            }
        }

        if (impulse.lengthSquared() <= 1.0e-12) {
            return;
        }
        target.setVelocity(target.getVelocity().add(impulse));
    }

    private record DamageKey(UUID attackerId, UUID targetId) {
    }
}
