package io.github.aliceblackrose.wynnclone.ability;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface AbilityAction {
    boolean execute(Player caster);
}
