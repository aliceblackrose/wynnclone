package io.github.aliceblackrose.wynnclone.ability.impl;

import io.github.aliceblackrose.wynnclone.ability.AbilityAction;
import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.TargetingService;
import java.util.Objects;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class BashAbility implements AbilityAction {
    private final TargetingService targeting;
    private final CombatDamageService damage;

    public BashAbility(TargetingService targeting, CombatDamageService damage) {
        this.targeting = Objects.requireNonNull(targeting, "targeting");
        this.damage = Objects.requireNonNull(damage, "damage");
    }

    @Override
    public boolean execute(Player caster) {
        caster.getWorld().spawnParticle(
                Particle.SWEEP_ATTACK,
                caster.getLocation().add(0.0, 1.0, 0.0),
                8,
                1.2,
                0.4,
                1.2,
                0.0);

        for (var target : targeting.cone(caster, 3.5, 0.15)) {
            damage.damage(caster, target, 6.0);
            damage.knockAway(caster, target, 0.65, 0.2);
        }
        return true;
    }
}
