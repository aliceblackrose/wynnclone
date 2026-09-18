package io.github.aliceblackrose.wynnclone.listener;

import io.github.aliceblackrose.wynnclone.ability.AbilityEngine;
import io.github.aliceblackrose.wynnclone.combat.BasicAttackService;
import io.github.aliceblackrose.wynnclone.combat.CombatClock;
import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.CombatFeedback;
import io.github.aliceblackrose.wynnclone.combat.CombatInput;
import io.github.aliceblackrose.wynnclone.combat.CombatStateStore;
import io.github.aliceblackrose.wynnclone.combat.ComboOutcome;
import io.github.aliceblackrose.wynnclone.combat.ComboService;
import java.util.Objects;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class MeleeCombatListener implements Listener {
    private final CombatStateStore states;
    private final ComboService combos;
    private final AbilityEngine abilities;
    private final BasicAttackService basicAttacks;
    private final CombatDamageService damage;
    private final CombatFeedback feedback;
    private final CombatClock clock;

    public MeleeCombatListener(
            CombatStateStore states,
            ComboService combos,
            AbilityEngine abilities,
            BasicAttackService basicAttacks,
            CombatDamageService damage,
            CombatFeedback feedback,
            CombatClock clock) {
        this.states = Objects.requireNonNull(states, "states");
        this.combos = Objects.requireNonNull(combos, "combos");
        this.abilities = Objects.requireNonNull(abilities, "abilities");
        this.basicAttacks = Objects.requireNonNull(basicAttacks, "basicAttacks");
        this.damage = Objects.requireNonNull(damage, "damage");
        this.feedback = Objects.requireNonNull(feedback, "feedback");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMeleeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)
                || !(event.getEntity() instanceof LivingEntity target)
                || target instanceof ArmorStand) {
            return;
        }

        if (damage.isInternal(attacker, target)) {
            return;
        }

        DamageCause cause = event.getCause();
        if (cause != DamageCause.ENTITY_ATTACK && cause != DamageCause.ENTITY_SWEEP_ATTACK) {
            return;
        }

        event.setCancelled(true);

        if (cause == DamageCause.ENTITY_SWEEP_ATTACK) {
            return;
        }

        var state = states.state(attacker.getUniqueId());
        long now = clock.nowMillis();

        if (combos.active(state.combo(), now)) {
            ComboOutcome outcome = combos.accept(state.combo(), CombatInput.LEFT, now);
            switch (outcome) {
                case ComboOutcome.Progress progress -> feedback.comboProgress(attacker, progress.inputs());
                case ComboOutcome.Cast cast -> abilities.cast(attacker, cast.abilityId());
                case ComboOutcome.Rejected ignored -> feedback.comboRejected(attacker);
            }
            return;
        }

        basicAttacks.attack(attacker, target);
    }
}
