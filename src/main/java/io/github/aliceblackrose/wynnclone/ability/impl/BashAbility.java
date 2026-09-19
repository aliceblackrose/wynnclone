package io.github.aliceblackrose.wynnclone.ability.impl;

import io.github.aliceblackrose.wynnclone.ability.AbilityAction;
import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import io.github.aliceblackrose.wynnclone.combat.area.AreaImpactRequest;
import io.github.aliceblackrose.wynnclone.combat.telegraph.AreaTelegraphService;
import io.github.aliceblackrose.wynnclone.combat.telegraph.AreaTelegraphSpec;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class BashAbility implements AbilityAction {
    private static final AreaTelegraphSpec TELEGRAPH = new AreaTelegraphSpec(
            350L,
            Particle.CRIT,
            Particle.SWEEP_ATTACK,
            new AreaImpactRequest(
                    2.25,
                    1.75,
                    DamageRequest.physical(6.0),
                    Optional.empty(),
                    0.65,
                    0.20));

    private final AreaTelegraphService telegraphs;

    public BashAbility(AreaTelegraphService telegraphs) {
        this.telegraphs = Objects.requireNonNull(telegraphs, "telegraphs");
    }

    @Override
    public boolean execute(Player caster) {
        Vector direction = caster.getLocation().getDirection().setY(0.0);
        if (direction.lengthSquared() < 1.0e-9) {
            return false;
        }

        var center = caster.getLocation().add(direction.normalize().multiply(2.0));
        return telegraphs.spawn(caster, center, TELEGRAPH);
    }
}
