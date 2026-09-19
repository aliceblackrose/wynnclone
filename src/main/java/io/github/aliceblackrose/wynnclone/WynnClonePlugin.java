package io.github.aliceblackrose.wynnclone;

import static io.github.aliceblackrose.wynnclone.combat.CombatInput.LEFT;
import static io.github.aliceblackrose.wynnclone.combat.CombatInput.RIGHT;

import io.github.aliceblackrose.wynnclone.ability.AbilityEngine;
import io.github.aliceblackrose.wynnclone.ability.CombatAbilities;
import io.github.aliceblackrose.wynnclone.ability.impl.ArcaneBoltAbility;
import io.github.aliceblackrose.wynnclone.ability.impl.BashAbility;
import io.github.aliceblackrose.wynnclone.ability.impl.BlinkAbility;
import io.github.aliceblackrose.wynnclone.ability.impl.DashAbility;
import io.github.aliceblackrose.wynnclone.combat.AbilityCatalog;
import io.github.aliceblackrose.wynnclone.combat.AbilityDefinition;
import io.github.aliceblackrose.wynnclone.combat.AbilityId;
import io.github.aliceblackrose.wynnclone.combat.BasicAttackService;
import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.CombatFeedback;
import io.github.aliceblackrose.wynnclone.combat.CombatStateStore;
import io.github.aliceblackrose.wynnclone.combat.ComboService;
import io.github.aliceblackrose.wynnclone.combat.DamageCalculator;
import io.github.aliceblackrose.wynnclone.combat.InputCombo;
import io.github.aliceblackrose.wynnclone.combat.SystemCombatClock;
import io.github.aliceblackrose.wynnclone.combat.TargetingService;
import io.github.aliceblackrose.wynnclone.combat.WeaponDefinitionResolver;
import io.github.aliceblackrose.wynnclone.command.CombatCommand;
import io.github.aliceblackrose.wynnclone.listener.CombatInputListener;
import io.github.aliceblackrose.wynnclone.listener.MeleeCombatListener;
import io.github.aliceblackrose.wynnclone.listener.PlayerLifecycleListener;
import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class WynnClonePlugin extends JavaPlugin {
    private CombatStateStore combatStates;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        long comboTimeoutMillis = getConfig().getLong("combat.combo-timeout-ms", 1200L);
        int maxComboLength = getConfig().getInt("combat.max-combo-length", 4);
        double maximumMana = getConfig().getDouble("mana.maximum", 100.0);
        double manaRegenerationPerSecond = getConfig().getDouble("mana.regeneration-per-second", 5.0);
        validateConfiguration(comboTimeoutMillis, maxComboLength, maximumMana, manaRegenerationPerSecond);

        var clock = new SystemCombatClock();
        var catalog = new AbilityCatalog();
        var abilities = new CombatAbilities(catalog);
        var feedback = new CombatFeedback();
        var targeting = new TargetingService();

        combatStates = new CombatStateStore(maximumMana);
        var damage = new CombatDamageService(new DamageCalculator(), combatStates);
        var comboService = new ComboService(catalog, comboTimeoutMillis, maxComboLength);
        var abilityEngine = new AbilityEngine(abilities, combatStates, clock, feedback, getLogger());
        var basicAttacks = new BasicAttackService(
                combatStates,
                clock,
                new WeaponDefinitionResolver(),
                damage);

        registerPrototypeAbilities(abilities, targeting, damage);

        getServer().getPluginManager().registerEvents(
                new CombatInputListener(combatStates, comboService, abilityEngine, feedback, clock),
                this);
        getServer().getPluginManager().registerEvents(
                new MeleeCombatListener(
                        combatStates,
                        comboService,
                        abilityEngine,
                        basicAttacks,
                        damage,
                        feedback,
                        clock),
                this);
        getServer().getPluginManager().registerEvents(new PlayerLifecycleListener(combatStates), this);

        getServer().getOnlinePlayers().forEach(player -> combatStates.state(player.getUniqueId()));
        startManaRegeneration(manaRegenerationPerSecond);

        PluginCommand combatCommand = Objects.requireNonNull(getCommand("combat"), "combat command missing from plugin.yml");
        var commandHandler = new CombatCommand(combatStates, abilities, abilityEngine);
        combatCommand.setExecutor(commandHandler);
        combatCommand.setTabCompleter(commandHandler);

        getLogger().info("Combat engine enabled with " + catalog.definitions().size() + " abilities.");
    }

    @Override
    public void onDisable() {
        if (combatStates != null) {
            combatStates.clear();
        }
    }

    private void registerPrototypeAbilities(
            CombatAbilities abilities,
            TargetingService targeting,
            CombatDamageService damage) {
        abilities.register(
                new AbilityDefinition(
                        new AbilityId("warrior.bash"),
                        "Bash",
                        InputCombo.of(RIGHT, LEFT, LEFT),
                        20.0,
                        1500L),
                new BashAbility(targeting, damage));

        abilities.register(
                new AbilityDefinition(
                        new AbilityId("warrior.dash"),
                        "Dash",
                        InputCombo.of(RIGHT, RIGHT, LEFT),
                        15.0,
                        2000L),
                new DashAbility());

        abilities.register(
                new AbilityDefinition(
                        new AbilityId("mage.arcane_bolt"),
                        "Arcane Bolt",
                        InputCombo.of(RIGHT, LEFT, RIGHT),
                        25.0,
                        1250L),
                new ArcaneBoltAbility(targeting, damage));

        abilities.register(
                new AbilityDefinition(
                        new AbilityId("mage.blink"),
                        "Blink",
                        InputCombo.of(RIGHT, RIGHT, RIGHT),
                        30.0,
                        4000L),
                new BlinkAbility());
    }

    private void startManaRegeneration(double manaRegenerationPerSecond) {
        double amountPerHalfSecond = manaRegenerationPerSecond / 2.0;
        getServer().getScheduler().runTaskTimer(
                this,
                () -> combatStates.values().forEach(state -> state.mana().restore(amountPerHalfSecond)),
                10L,
                10L);
    }

    private static void validateConfiguration(
            long comboTimeoutMillis,
            int maxComboLength,
            double maximumMana,
            double manaRegenerationPerSecond) {
        if (comboTimeoutMillis < 0L) {
            throw new IllegalStateException("combat.combo-timeout-ms must be non-negative");
        }
        if (maxComboLength < 1) {
            throw new IllegalStateException("combat.max-combo-length must be positive");
        }
        if (!Double.isFinite(maximumMana) || maximumMana <= 0.0) {
            throw new IllegalStateException("mana.maximum must be finite and positive");
        }
        if (!Double.isFinite(manaRegenerationPerSecond) || manaRegenerationPerSecond < 0.0) {
            throw new IllegalStateException("mana.regeneration-per-second must be finite and non-negative");
        }
    }
}
