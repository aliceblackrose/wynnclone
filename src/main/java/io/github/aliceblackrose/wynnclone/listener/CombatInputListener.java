package io.github.aliceblackrose.wynnclone.listener;

import io.github.aliceblackrose.wynnclone.ability.AbilityEngine;
import io.github.aliceblackrose.wynnclone.combat.CombatClock;
import io.github.aliceblackrose.wynnclone.combat.CombatFeedback;
import io.github.aliceblackrose.wynnclone.combat.CombatInput;
import io.github.aliceblackrose.wynnclone.combat.CombatStateStore;
import io.github.aliceblackrose.wynnclone.combat.ComboOutcome;
import io.github.aliceblackrose.wynnclone.combat.ComboService;
import java.util.Objects;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class CombatInputListener implements Listener {
    private final CombatStateStore states;
    private final ComboService combos;
    private final AbilityEngine abilities;
    private final CombatFeedback feedback;
    private final CombatClock clock;

    public CombatInputListener(
            CombatStateStore states,
            ComboService combos,
            AbilityEngine abilities,
            CombatFeedback feedback,
            CombatClock clock) {
        this.states = Objects.requireNonNull(states, "states");
        this.combos = Objects.requireNonNull(combos, "combos");
        this.abilities = Objects.requireNonNull(abilities, "abilities");
        this.feedback = Objects.requireNonNull(feedback, "feedback");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        CombatInput input = toCombatInput(event.getAction());
        if (input == null) {
            return;
        }

        var player = event.getPlayer();
        var state = states.state(player.getUniqueId());

        if (input == CombatInput.LEFT && !state.combo().active()) {
            return;
        }

        // Preserve normal block interaction. Prototype spell inputs are performed in the air.
        if (input == CombatInput.RIGHT && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ComboOutcome outcome = combos.accept(state.combo(), input, clock.nowMillis());
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        switch (outcome) {
            case ComboOutcome.Progress progress -> feedback.comboProgress(player, progress.inputs());
            case ComboOutcome.Cast cast -> abilities.cast(player, cast.abilityId());
            case ComboOutcome.Rejected ignored -> feedback.comboRejected(player);
        }
    }

    private static CombatInput toCombatInput(Action action) {
        return switch (action) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> CombatInput.LEFT;
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> CombatInput.RIGHT;
            default -> null;
        };
    }
}
