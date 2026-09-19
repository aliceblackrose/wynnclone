package io.github.aliceblackrose.wynnclone.combat.status;

import io.github.aliceblackrose.wynnclone.combat.CombatClock;
import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.DamageRequest;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class StatusEffectService {
    private final Server server;
    private final CombatClock clock;
    private final CombatDamageService damage;
    private final Map<UUID, StatusEffectTracker> trackers = new HashMap<>();

    public StatusEffectService(Server server, CombatClock clock, CombatDamageService damage) {
        this.server = Objects.requireNonNull(server, "server");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.damage = Objects.requireNonNull(damage, "damage");
    }

    public void apply(Player source, LivingEntity target, StatusEffectSpec spec) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(spec, "spec");

        if (!target.isValid() || target.isDead()) {
            return;
        }

        trackers.computeIfAbsent(target.getUniqueId(), ignored -> new StatusEffectTracker())
                .apply(source.getUniqueId(), spec, clock.nowMillis());
    }

    public void tick() {
        long now = clock.nowMillis();
        var iterator = trackers.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (!(server.getEntity(entry.getKey()) instanceof LivingEntity target)
                    || !target.isValid()
                    || target.isDead()) {
                iterator.remove();
                continue;
            }

            StatusEffectTracker tracker = entry.getValue();
            for (StatusEffectTick tick : tracker.pollDueTicks(now)) {
                applyTick(target, tick);
            }

            if (tracker.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public Set<StatusEffectType> activeTypes(UUID targetId) {
        Objects.requireNonNull(targetId, "targetId");
        StatusEffectTracker tracker = trackers.get(targetId);
        if (tracker == null) {
            return Set.of();
        }

        Set<StatusEffectType> types = tracker.activeTypes();
        return types.isEmpty() ? Set.of() : EnumSet.copyOf(types);
    }

    public void clear() {
        trackers.clear();
    }

    private void applyTick(LivingEntity target, StatusEffectTick tick) {
        Player source = server.getPlayer(tick.sourceId());
        if (source == null || !source.isOnline()) {
            return;
        }

        DamageRequest request = switch (tick.type()) {
            case BURN -> DamageRequest.magic(tick.strength()).withoutCriticals();
            case BLEED -> DamageRequest.physical(tick.strength()).withoutCriticals();
        };

        damage.damage(source, target, request);

        Particle particle = switch (tick.type()) {
            case BURN -> Particle.FLAME;
            case BLEED -> Particle.CRIT;
        };
        target.getWorld().spawnParticle(
                particle,
                target.getLocation().add(0.0, 1.0, 0.0),
                5,
                0.25,
                0.35,
                0.25,
                0.02);
    }
}
