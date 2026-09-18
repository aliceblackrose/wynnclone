package io.github.aliceblackrose.wynnclone.ability;

import io.github.aliceblackrose.wynnclone.combat.AbilityId;
import io.github.aliceblackrose.wynnclone.combat.CombatClock;
import io.github.aliceblackrose.wynnclone.combat.CombatFeedback;
import io.github.aliceblackrose.wynnclone.combat.CombatStateStore;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public final class AbilityEngine {
    private final CombatAbilities abilities;
    private final CombatStateStore states;
    private final CombatClock clock;
    private final CombatFeedback feedback;
    private final Logger logger;

    public AbilityEngine(
            CombatAbilities abilities,
            CombatStateStore states,
            CombatClock clock,
            CombatFeedback feedback,
            Logger logger) {
        this.abilities = Objects.requireNonNull(abilities, "abilities");
        this.states = Objects.requireNonNull(states, "states");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.feedback = Objects.requireNonNull(feedback, "feedback");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public CastResult cast(Player player, AbilityId abilityId) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(abilityId, "abilityId");

        var definition = abilities.definition(abilityId).orElse(null);
        var action = abilities.action(abilityId).orElse(null);
        if (definition == null || action == null) {
            return CastResult.UNKNOWN_ABILITY;
        }

        var state = states.state(player.getUniqueId());
        long now = clock.nowMillis();
        long remaining = state.cooldowns().remainingMillis(abilityId, now);
        if (remaining > 0L) {
            feedback.cooldown(player, remaining);
            return CastResult.ON_COOLDOWN;
        }
        if (!state.mana().canSpend(definition.manaCost())) {
            feedback.insufficientMana(player);
            return CastResult.INSUFFICIENT_MANA;
        }

        final boolean accepted;
        try {
            accepted = action.execute(player);
        } catch (RuntimeException exception) {
            logger.log(Level.SEVERE, "Ability execution failed for " + abilityId + " and player " + player.getUniqueId(), exception);
            feedback.failed(player);
            return CastResult.FAILED;
        }

        if (!accepted) {
            feedback.failed(player);
            return CastResult.REJECTED;
        }
        if (!state.mana().spend(definition.manaCost())) {
            throw new IllegalStateException("Mana changed unexpectedly during synchronous ability execution");
        }

        state.cooldowns().start(abilityId, now, definition.cooldownMillis());
        feedback.castSuccess(player, definition, state);
        return CastResult.SUCCESS;
    }
}
