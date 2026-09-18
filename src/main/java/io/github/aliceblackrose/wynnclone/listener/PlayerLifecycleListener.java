package io.github.aliceblackrose.wynnclone.listener;

import io.github.aliceblackrose.wynnclone.combat.CombatStateStore;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerLifecycleListener implements Listener {
    private final CombatStateStore states;

    public PlayerLifecycleListener(CombatStateStore states) {
        this.states = Objects.requireNonNull(states, "states");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        states.state(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        states.remove(event.getPlayer().getUniqueId());
    }
}
