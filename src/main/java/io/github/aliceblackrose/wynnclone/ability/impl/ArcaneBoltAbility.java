package io.github.aliceblackrose.wynnclone.ability.impl;

import io.github.aliceblackrose.wynnclone.ability.AbilityAction;
import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import io.github.aliceblackrose.wynnclone.combat.projectile.ProjectileSpec;
import io.github.aliceblackrose.wynnclone.combat.projectile.VirtualProjectileService;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectSpec;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectType;
import io.github.aliceblackrose.wynnclone.combat.status.StatusStackPolicy;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class ArcaneBoltAbility implements AbilityAction {
    private static final ProjectileSpec PROJECTILE = new ProjectileSpec(
            1.35,
            14.0,
            0.45,
            DamageRequest.magic(8.0),
            Particle.END_ROD,
            Particle.END_ROD,
            Optional.of(new StatusEffectSpec(
                    StatusEffectType.BURN,
                    1.25,
                    2000L,
                    500L,
                    StatusStackPolicy.REFRESH)),
            2,
            0.0);

    private final VirtualProjectileService projectiles;

    public ArcaneBoltAbility(VirtualProjectileService projectiles) {
        this.projectiles = Objects.requireNonNull(projectiles, "projectiles");
    }

    @Override
    public boolean execute(Player caster) {
        return projectiles.spawn(caster, PROJECTILE);
    }
}
