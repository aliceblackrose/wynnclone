package io.github.aliceblackrose.wynnclone.combat;

import java.util.Objects;

public record AbilityDefinition(
        AbilityId id,
        String displayName,
        InputCombo combo,
        double manaCost,
        long cooldownMillis) {

    public AbilityDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(combo, "combo");

        displayName = displayName.trim();
        if (displayName.isEmpty()) {
            throw new IllegalArgumentException("Ability display name must not be blank");
        }
        if (!Double.isFinite(manaCost) || manaCost < 0.0) {
            throw new IllegalArgumentException("Mana cost must be finite and non-negative");
        }
        if (cooldownMillis < 0L) {
            throw new IllegalArgumentException("Cooldown must be non-negative");
        }
    }
}
