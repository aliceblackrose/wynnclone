package io.github.aliceblackrose.wynnclone.combat.telegraph;

import io.github.aliceblackrose.wynnclone.combat.CombatClock;
import io.github.aliceblackrose.wynnclone.combat.area.AreaImpactService;
import io.github.aliceblackrose.wynnclone.combat.projectile.Vec3;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class AreaTelegraphService {
    private static final int RING_POINTS = 16;

    private final Server server;
    private final CombatClock clock;
    private final AreaImpactService impacts;
    private final List<ActiveAreaTelegraph> active = new ArrayList<>();

    public AreaTelegraphService(Server server, CombatClock clock, AreaImpactService impacts) {
        this.server = Objects.requireNonNull(server, "server");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.impacts = Objects.requireNonNull(impacts, "impacts");
    }

    public boolean spawn(Player owner, Location center, AreaTelegraphSpec spec) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(spec, "spec");

        World world = center.getWorld();
        if (world == null || world != owner.getWorld()) {
            return false;
        }

        long impactAt;
        try {
            impactAt = Math.addExact(clock.nowMillis(), spec.delayMillis());
        } catch (ArithmeticException exception) {
            impactAt = Long.MAX_VALUE;
        }

        active.add(new ActiveAreaTelegraph(
                owner.getUniqueId(),
                world.getUID(),
                new Vec3(center.getX(), center.getY(), center.getZ()),
                impactAt,
                spec));
        return true;
    }

    public void tick() {
        long now = clock.nowMillis();
        Iterator<ActiveAreaTelegraph> iterator = active.iterator();

        while (iterator.hasNext()) {
            ActiveAreaTelegraph telegraph = iterator.next();
            Player owner = server.getPlayer(telegraph.ownerId());
            World world = server.getWorld(telegraph.worldId());

            if (owner == null
                    || !owner.isOnline()
                    || owner.isDead()
                    || world == null
                    || owner.getWorld() != world) {
                iterator.remove();
                continue;
            }

            Location center = toLocation(world, telegraph.center());
            if (now >= telegraph.impactAtMillis()) {
                renderImpact(center, telegraph.spec());
                impacts.apply(owner, center, telegraph.spec().impact());
                iterator.remove();
                continue;
            }

            renderWarning(center, telegraph.spec());
        }
    }

    public int activeCount() {
        return active.size();
    }

    public void clear() {
        active.clear();
    }

    private static void renderWarning(Location center, AreaTelegraphSpec spec) {
        double radius = spec.impact().radius();

        for (int index = 0; index < RING_POINTS; index++) {
            double angle = (Math.PI * 2.0 * index) / RING_POINTS;
            Location point = center.clone().add(
                    Math.cos(angle) * radius,
                    0.08,
                    Math.sin(angle) * radius);
            center.getWorld().spawnParticle(
                    spec.warningParticle(),
                    point,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0);
        }
    }

    private static void renderImpact(Location center, AreaTelegraphSpec spec) {
        center.getWorld().spawnParticle(
                spec.impactParticle(),
                center.clone().add(0.0, 0.3, 0.0),
                18,
                spec.impact().radius() * 0.45,
                0.25,
                spec.impact().radius() * 0.45,
                0.02);
    }

    private static Location toLocation(World world, Vec3 point) {
        return new Location(world, point.x(), point.y(), point.z());
    }
}
