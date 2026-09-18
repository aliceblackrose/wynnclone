package io.github.aliceblackrose.wynnclone.ability.impl;

import io.github.aliceblackrose.wynnclone.ability.AbilityAction;
import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import io.github.aliceblackrose.wynnclone.combat.TargetingService;
import java.util.Objects;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class ArcaneBoltAbility implements AbilityAction {
    private static final double RANGE = 14.0;

    private final TargetingService targeting;
    private final CombatDamageService damage;

    public ArcaneBoltAbility(TargetingService targeting, CombatDamageService damage) {
        this.targeting = Objects.requireNonNull(targeting, "targeting");
        this.damage = Objects.requireNonNull(damage, "damage");
    }

    @Override
    public boolean execute(Player caster) {
        var start = caster.getEyeLocation();
        Vector direction = start.getDirection();
        if (direction.lengthSquared() < 1.0e-9) {
            return false;
        }
        direction.normalize();

        var target = targeting.rayTarget(caster, RANGE, 0.45);
        double visualRange = target.map(entity -> entity.getEyeLocation().distance(start)).orElse(RANGE);

        for (double distance = 0.75; distance <= visualRange; distance += 0.75) {
            var point = start.clone().add(direction.clone().multiply(distance));
            caster.getWorld().spawnParticle(Particle.END_ROD, point, 1, 0.0, 0.0, 0.0, 0.0);
        }

        target.ifPresent(entity -> damage.damage(caster, entity, DamageRequest.magic(8.0)));
        return true;
    }
}
