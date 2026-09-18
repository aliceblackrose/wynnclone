package io.github.aliceblackrose.wynnclone.combat;

import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class CombatFeedback {
    public void comboProgress(Player player, List<CombatInput> inputs) {
        String combo = inputs.stream()
                .map(CombatInput::symbol)
                .reduce((left, right) -> left + "-" + right)
                .orElse("");
        player.sendActionBar(Component.text("Combo: " + combo, NamedTextColor.AQUA));
    }

    public void comboRejected(Player player) {
        player.sendActionBar(Component.text("Invalid combo", NamedTextColor.RED));
    }

    public void castSuccess(Player player, AbilityDefinition ability, PlayerCombatState state) {
        String mana = String.format(Locale.ROOT, "%.0f/%.0f", state.mana().current(), state.mana().maximum());
        player.sendActionBar(Component.text(ability.displayName() + "  Mana " + mana, NamedTextColor.GREEN));
    }

    public void insufficientMana(Player player) {
        player.sendActionBar(Component.text("Not enough mana", NamedTextColor.RED));
    }

    public void cooldown(Player player, long remainingMillis) {
        player.sendActionBar(Component.text(
                String.format(Locale.ROOT, "Cooldown %.1fs", remainingMillis / 1000.0),
                NamedTextColor.YELLOW));
    }

    public void failed(Player player) {
        player.sendActionBar(Component.text("Ability failed", NamedTextColor.RED));
    }
}
