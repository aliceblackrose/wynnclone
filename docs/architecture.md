# Combat architecture

The project remains combat-only.

```text
                  combat input
                       |
                       v
                  ComboService
                       |
                       v
                  AbilityEngine
                 /             \
                v               v
       direct/movement     delayed/ranged
                            /          \
                           v            v
                AreaTelegraphService   VirtualProjectileService
                           |            |
                           v            v
                    AreaImpactService   segment ray trace
                           ^            |
                           |       +----+----------+
                           |       |               |
                           |       v               v
                           |   direct hit       explosion
                           |       |               |
                           +-------+---------------+
                                   |
                                   v
                         CombatDamageService
                                   |
                                   v
                            DamageCalculator
```

## Central ticker

```text
CombatTicker (1/tick)
  ├─ projectiles
  ├─ status effects
  ├─ area telegraphs
  └─ mana regeneration cadence
```

No combat subsystem creates independent repeating tasks.

## Area impact boundary

`AreaImpactRequest` is the canonical definition for circular combat impacts.

It carries:

- radius
- vertical radius
- damage
- optional on-hit status
- horizontal knockback
- vertical knockback

`AreaImpactService` resolves nearby living entities using bounded world queries and performs a horizontal-radius check before applying the request.

Telegraphs use this directly.

Explosive projectiles construct the same request from their projectile damage/status configuration.

## Telegraph lifecycle

`AreaTelegraphService` stores:

- owner UUID
- world UUID
- immutable center vector
- monotonic impact timestamp
- immutable telegraph specification

Every tick it either:

1. removes invalid/disconnected/world-changed owners
2. renders the warning ring
3. or resolves the impact exactly once and removes the telegraph

Bash is the first consumer:

```text
cast Bash
   |
   v
center = 2 blocks forward
   |
   v
350 ms warning ring
   |
   v
2.25-block area impact
   |
   +-- physical damage
   +-- horizontal knockback
   +-- vertical lift
```

## Projectile lifecycle

An active virtual projectile contains:

- owner UUID
- world UUID
- current position
- normalized direction
- distance traveled
- immutable projectile spec
- UUID set of entities already hit

For each tick:

1. determine the movement segment for this tick
2. ray-trace the segment
3. if nothing is hit, advance normally
4. if a block is hit, terminate
5. if an entity is hit, apply the configured impact
6. for piercing projectiles, record that entity UUID and continue the unused portion of the same segment
7. terminate once the distinct-hit limit or travel limit is reached

A small forward nudge is applied after a piercing hit to guarantee forward progress even when the collision position lies directly on an entity boundary.

Already-hit UUIDs are excluded from subsequent ray traces.

## Explosive projectiles

`ProjectileSpec.explosionRadius > 0` changes collision semantics:

- first entity/block collision terminates the projectile
- the normal direct-hit path is replaced by a circular `AreaImpactService` call
- damage and optional on-hit status are reused from the projectile definition

An explosive specification must use `maxEntityHits == 1`. Invalid combinations fail during construction instead of producing ambiguous runtime behavior.

## Ability responsibilities

Abilities still orchestrate rather than implement engine mechanics.

`ArcaneBoltAbility` only declares an immutable projectile specification and asks the projectile service to spawn it.

`BashAbility` only calculates a point in front of the caster and asks the telegraph service to spawn its immutable area specification.

Neither ability owns:

- target iteration
- damage formulas
- critical calculations
- defense calculations
- status tracking
- collision iteration
- scheduler tasks

## Defensive rules

- World changes invalidate active projectile/telegraph state owned by that player.
- Projectiles cannot record the same entity twice.
- Hit limits must be positive.
- Explosion radius must be finite and non-negative.
- Explosive multi-hit combinations are rejected.
- AoE radius/vertical bounds/knockback values are validated at construction.
- Zero horizontal displacement still permits vertical knockback.
- World/entity references are resolved at tick time rather than retained in long-lived combat state.
