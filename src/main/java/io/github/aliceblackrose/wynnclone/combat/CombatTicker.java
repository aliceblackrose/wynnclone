package io.github.aliceblackrose.wynnclone.combat;

import io.github.aliceblackrose.wynnclone.combat.projectile.VirtualProjectileService;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectService;
import java.util.Objects;

public final class CombatTicker {
    private static final int MANA_REGEN_INTERVAL_TICKS = 10;

    private final CombatStateStore states;
    private final VirtualProjectileService projectiles;
    private final StatusEffectService statusEffects;
    private final double manaPerHalfSecond;
    private int ticksUntilManaRegen = MANA_REGEN_INTERVAL_TICKS;

    public CombatTicker(
            CombatStateStore states,
            VirtualProjectileService projectiles,
            StatusEffectService statusEffects,
            double manaRegenerationPerSecond) {
        this.states = Objects.requireNonNull(states, "states");
        this.projectiles = Objects.requireNonNull(projectiles, "projectiles");
        this.statusEffects = Objects.requireNonNull(statusEffects, "statusEffects");

        if (!Double.isFinite(manaRegenerationPerSecond) || manaRegenerationPerSecond < 0.0) {
            throw new IllegalArgumentException("Mana regeneration must be finite and non-negative");
        }
        this.manaPerHalfSecond = manaRegenerationPerSecond / 2.0;
    }

    public void tick() {
        projectiles.tick();
        statusEffects.tick();

        ticksUntilManaRegen--;
        if (ticksUntilManaRegen > 0) {
            return;
        }

        ticksUntilManaRegen = MANA_REGEN_INTERVAL_TICKS;
        if (manaPerHalfSecond == 0.0) {
            return;
        }

        states.values().forEach(state -> state.mana().restore(manaPerHalfSecond));
    }
}
