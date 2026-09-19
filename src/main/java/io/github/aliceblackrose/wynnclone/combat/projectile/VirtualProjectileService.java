package io.github.aliceblackrose.wynnclone.combat.projectile;

import io.github.aliceblackrose.wynnclone.combat.CombatDamageService;
import io.github.aliceblackrose.wynnclone.combat.area.AreaImpactRequest;
import io.github.aliceblackrose.wynnclone.combat.area.AreaImpactService;
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
    private static final double MIN_ADVANCE = 0.01;

    private final Server server;
    private final CombatDamageService damage;
    private final StatusEffectService statusEffects;
    private final AreaImpactService areaImpacts;
    private final List<ActiveProjectile> active = new ArrayList<>();

    public VirtualProjectileService(
            Server server,
            CombatDamageService damage,
            StatusEffectService statusEffects,
            AreaImpactService areaImpacts) {
        this.server = Objects.requireNonNull(server, "server");
        this.damage = Objects.requireNonNull(damage, "damage");
        this.statusEffects = Objects.requireNonNull(statusEffects, "statusEffects");
        this.areaImpacts = Objects.requireNonNull(areaImpacts, "areaImpacts");
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

        spawnTrail(owner.getWorld(), start, spec);
        return true;
    }

    public void tick() {
        Iterator<ActiveProjectile> iterator = active.iterator();

        while (iterator.hasNext()) {
            ActiveProjectile projectile = iterator.next();
            Player owner = server.getPlayer(projectile.ownerId());
            World world = server.getWorld(projectile.worldId());

            if (owner == null
                    || !owner.isOnline()
                    || owner.isDead()
                    || world == null
                    || owner.getWorld() != world) {
                iterator.remove();
                continue;
            }

            double remaining = projectile.remainingDistance();
            if (remaining <= 1.0e-9) {
                iterator.remove();
                continue;
            }

            double segmentRemaining = Math.min(projectile.spec().speedPerTick(), remaining);
            boolean remove = processSegment(owner, world, projectile, segmentRemaining);

            if (remove || projectile.exhausted()) {
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

    private boolean processSegment(
            Player owner,
            World world,
            ActiveProjectile projectile,
            double segmentDistance) {
        double remainingInSegment = segmentDistance;
        int collisionGuard = projectile.spec().maxEntityHits() + 2;

        while (remainingInSegment > 1.0e-9 && collisionGuard-- > 0) {
            Location start = toLocation(world, projectile.position());
            Vector direction = toVector(projectile.direction());

            var hit = world.rayTrace(
                    start,
                    direction,
                    remainingInSegment,
                    FluidCollisionMode.NEVER,
                    true,
                    projectile.spec().collisionRadius(),
                    entity -> isCombatTarget(owner, projectile, entity));

            if (hit == null) {
                Vec3 next = projectile.advance(remainingInSegment);
                spawnTrail(world, toLocation(world, next), projectile.spec());
                return false;
            }

            Location impact = new Location(
                    world,
                    hit.getHitPosition().getX(),
                    hit.getHitPosition().getY(),
                    hit.getHitPosition().getZ());
            double distanceToHit = start.distance(impact);

            if (distanceToHit > 1.0e-9) {
                double advance = Math.min(distanceToHit, remainingInSegment);
                projectile.advance(advance);
                remainingInSegment -= advance;
            }

            spawnImpact(world, impact, projectile.spec());

            if (hit.getHitEntity() instanceof LivingEntity target) {
                if (projectile.spec().explosionRadius() > 0.0) {
                    applyExplosion(owner, impact, projectile.spec());
                    return true;
                }

                damage.damage(owner, target, projectile.spec().damage());
                projectile.spec().onHitEffect().ifPresent(effect -> statusEffects.apply(owner, target, effect));
                projectile.recordHit(target.getUniqueId());

                if (projectile.hitLimitReached()) {
                    return true;
                }

                double nudge = Math.min(
                        MIN_ADVANCE,
                        Math.min(remainingInSegment, projectile.remainingDistance()));
                if (nudge <= 1.0e-9) {
                    return projectile.exhausted();
                }

                projectile.advance(nudge);
                remainingInSegment -= nudge;
                continue;
            }

            if (projectile.spec().explosionRadius() > 0.0) {
                applyExplosion(owner, impact, projectile.spec());
            }
            return true;
        }

        return collisionGuard <= 0;
    }

    private void applyExplosion(Player owner, Location impact, ProjectileSpec spec) {
        areaImpacts.apply(
                owner,
                impact,
                new AreaImpactRequest(
                        spec.explosionRadius(),
                        spec.explosionRadius(),
                        spec.damage(),
                        spec.onHitEffect(),
                        0.0,
                        0.0));
    }

    private static boolean isCombatTarget(
            Player owner,
            ActiveProjectile projectile,
            org.bukkit.entity.Entity entity) {
        return entity instanceof LivingEntity
                && entity != owner
                && !(entity instanceof ArmorStand)
                && entity.isValid()
                && !entity.isDead()
                && !projectile.hasHit(entity.getUniqueId());
    }

    private static void spawnTrail(World world, Location location, ProjectileSpec spec) {
        world.spawnParticle(
                spec.trailParticle(),
                location,
                2,
                0.04,
                0.04,
                0.04,
                0.0);
    }

    private static void spawnImpact(World world, Location location, ProjectileSpec spec) {
        world.spawnParticle(
                spec.impactParticle(),
                location,
                8,
                0.2,
                0.2,
                0.2,
                0.03);
    }

    private static Location toLocation(World world, Vec3 point) {
        return new Location(world, point.x(), point.y(), point.z());
    }

    private static Vector toVector(Vec3 point) {
        return new Vector(point.x(), point.y(), point.z());
    }
}
