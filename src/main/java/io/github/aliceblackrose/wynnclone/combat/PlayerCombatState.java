package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public final class PlayerCombatState {
    private final ManaPool mana;
    private final CooldownTracker cooldowns = new CooldownTracker();
    private final ComboBuffer combo = new ComboBuffer();
    private final AttackCooldown basicAttackCooldown = new AttackCooldown();
    private CombatStats stats = CombatStats.defaults();

    public PlayerCombatState(double maximumMana) {
        this.mana = new ManaPool(maximumMana);
    }

    public ManaPool mana() {
        return mana;
    }

    public CooldownTracker cooldowns() {
        return cooldowns;
    }

    public ComboBuffer combo() {
        return combo;
    }

    public AttackCooldown basicAttackCooldown() {
        return basicAttackCooldown;
    }

    public CombatStats stats() {
        return stats;
    }

    public void setStats(CombatStats stats) {
        this.stats = Objects.requireNonNull(stats, "stats");
    }
}
