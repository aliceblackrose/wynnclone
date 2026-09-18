package io.github.aliceblackrose.wynnclone.combat;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CombatStateStore {
    private final Map<UUID, PlayerCombatState> states = new HashMap<>();
    private final double maximumMana;

    public CombatStateStore(double maximumMana) {
        if (!Double.isFinite(maximumMana) || maximumMana <= 0.0) {
            throw new IllegalArgumentException("Maximum mana must be finite and positive");
        }
        this.maximumMana = maximumMana;
    }

    public PlayerCombatState state(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return states.computeIfAbsent(playerId, ignored -> new PlayerCombatState(maximumMana));
    }

    public void remove(UUID playerId) {
        states.remove(Objects.requireNonNull(playerId, "playerId"));
    }

    public Collection<PlayerCombatState> values() {
        return List.copyOf(states.values());
    }

    public void clear() {
        states.clear();
    }
}
