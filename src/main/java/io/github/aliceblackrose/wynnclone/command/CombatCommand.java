package io.github.aliceblackrose.wynnclone.command;

import io.github.aliceblackrose.wynnclone.ability.AbilityEngine;
import io.github.aliceblackrose.wynnclone.ability.CombatAbilities;
import io.github.aliceblackrose.wynnclone.combat.AbilityId;
import io.github.aliceblackrose.wynnclone.combat.CombatInput;
import io.github.aliceblackrose.wynnclone.combat.CombatStateStore;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class CombatCommand implements CommandExecutor, TabCompleter {
    private final CombatStateStore states;
    private final CombatAbilities abilities;
    private final AbilityEngine engine;

    public CombatCommand(CombatStateStore states, CombatAbilities abilities, AbilityEngine engine) {
        this.states = Objects.requireNonNull(states, "states");
        this.abilities = Objects.requireNonNull(abilities, "abilities");
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command requires a player.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(Component.text("/combat <mana|stats|combo|cast>", NamedTextColor.YELLOW));
            return true;
        }

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "mana" -> showMana(player);
            case "stats" -> showStats(player);
            case "combo" -> showCombo(player);
            case "cast" -> cast(player, args);
            default -> false;
        };
    }

    private boolean showMana(Player player) {
        var mana = states.state(player.getUniqueId()).mana();
        player.sendMessage(Component.text(
                String.format(Locale.ROOT, "Mana: %.1f / %.1f", mana.current(), mana.maximum()),
                NamedTextColor.AQUA));
        return true;
    }

    private boolean showStats(Player player) {
        var stats = states.state(player.getUniqueId()).stats();
        String text = String.format(
                Locale.ROOT,
                "Physical x%.2f | Magic x%.2f | Crit %.1f%% x%.2f | Defense %.1f",
                stats.physicalDamageMultiplier(),
                stats.magicDamageMultiplier(),
                stats.criticalChance() * 100.0,
                stats.criticalDamageMultiplier(),
                stats.defense());
        player.sendMessage(Component.text(text, NamedTextColor.AQUA));
        return true;
    }

    private boolean showCombo(Player player) {
        List<CombatInput> inputs = states.state(player.getUniqueId()).combo().inputs();
        String combo = inputs.isEmpty()
                ? "(empty)"
                : inputs.stream().map(CombatInput::symbol).reduce((a, b) -> a + "-" + b).orElse("(empty)");
        player.sendMessage(Component.text("Combo: " + combo, NamedTextColor.AQUA));
        return true;
    }

    private boolean cast(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Usage: /combat cast <ability-id>", NamedTextColor.YELLOW));
            return true;
        }

        AbilityId id = new AbilityId(args[1]);
        if (abilities.definition(id).isEmpty()) {
            player.sendMessage(Component.text("Unknown ability: " + id, NamedTextColor.RED));
            return true;
        }
        engine.cast(player, id);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return List.of("mana", "stats", "combo", "cast").stream()
                    .filter(value -> value.startsWith(prefix))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("cast")) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            return List.of("warrior.bash", "warrior.dash", "mage.arcane_bolt", "mage.blink").stream()
                    .filter(value -> value.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
