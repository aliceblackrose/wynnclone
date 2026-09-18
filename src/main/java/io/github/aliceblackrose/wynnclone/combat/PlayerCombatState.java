package io.github.aliceblackrose.wynnclone.combat;

public final class PlayerCombatState {

    private final ManaPool mana;
    private final CooldownTracker cooldowns = new CooldownTracker();
    private final ComboBuffer combo = new ComboBuffer();

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
}
