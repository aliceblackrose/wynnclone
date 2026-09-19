package io.github.aliceblackrose.wynnclone.combat.projectile;

import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.status.StatusEffectService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class VirtualProjectileService {
    private final Server server;
    private final CombatDamageService damage;
    private final StatusEffectService statusEffects;
    private final List<ActiveProjectile> active = new ArrayList<>();

    public VirtualProjectileService(
            Server server,
            CombatDamageService damage,
            StatusEffectService statusEffects) {
        this.server = Objects.requireNonNull(server, "server");
        this.damage = Objects.requireNonNull(damage, "damage");
        this.statusEffects = Objects.requireNonNull(statusEffects, "statusEffects");
    }

    public boolean spawn(Player owner, ProjectileSpec spec) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(spec, "spec");

        Vector direction = owner.getEyeLocation().getDirection();
        if (direction.lengthSquared() < 1.0e-9) {
            return false;
        }

        Location start = owner.getEyeLocation();
        active.add(new ActiveProjectile(
                owner.getUniqueId(),
                owner.getWorld().getUID(),
                new Vec3(start.getX(), start.getY(), start.getZ()),
                new Vec3(direction.getX(), direction.getY(), direction.getZ()),
                spec));

        owner.getWorld().spawnParticle(
                spec.trailParticle(),
                start,
                2,
                0.04,
                0.04,
                0.04,
                0.0);
        return true;
    }

    public void tick() {
        Iterator<ActiveProjectile> iterator = active.iterator();

        while (iterator.hasNext()) {
            ActiveProjectile projectile = iterator.next();
            Player owner = server.getPlayer(projectile.ownerId());
            World world = server.getWorld(projectile.worldId());

            if (owner == null || !owner.isOnline() || owner.isDead() || world == null) {
                iterator.remove();
                continue;
            }

            double remaining = projectile.remainingDistance();
            if (remaining <= 1.0e-9) {
                iterator.remove();
                continue;
            }

            double stepDistance = Math.min(projectile.spec().speedPerTick(), remaining);
            Location start = toLocation(world, projectile.position());
            Vector direction = toVector(projectile.direction());

            var hit = world.rayTrace(
                    start,
                    direction,
                    stepDistance,
                    FluidCollisionMode.NEVER,
                    true,
                    projectile.spec().collisionRadius(),
                    entity -> isCombatTarget(owner, entity));

            if (hit != null) {
                Location impact = new Location(
                        world,
                        hit.getHitPosition().getX(),
                        hit.getHitPosition().getY(),
                        hit.getHitPosition().getZ());
                world.spawnParticle(
                        projectile.spec().impactParticle(),
                        impact,
                        8,
                        0.2,
                        0.2,
                        0.2,
                        0.03);

                if (hit.getHitEntity() instanceof LivingEntity target) {
                    damage.damage(owner, target, projectile.spec().damage());
                    projectile.spec().onHitEffect().ifPresent(effect -> statusEffects.apply(owner, target, effect));
                }

                iterator.remove();
                continue;
            }

            Vec3 next = projectile.advance(stepDistance);
            world.spawnParticle(
                    projectile.spec().trailParticle(),
                    toLocation(world, next),
                    2,
                    0.04,
                    0.04,
                    0.04,
                    0.0);

            if (projectile.exhausted()) {
                iterator.remove();
            }
        }
    }

    public int activeCount() {
        return active.size();
    }

    public void clear() {
        active.clear();
    }

    private static boolean isCombatTarget(Player owner, org.bukkit.entity.Entity entity) {
        return entity instanceof LivingEntity
                && entity != owner
                && !(entity instanceof ArmorStand)
                && entity.isValid()
                && !entity.isDead();
    }

    private static Location toLocation(World world, Vec3 point) {
        return new Location(world, point.x(), point.y(), point.z());
    }

    private static Vector toVector(Vec3 point) {
        return new Vector(point.x(), point.y(), point.z());
    }
}
