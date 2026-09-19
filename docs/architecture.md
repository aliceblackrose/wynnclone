# Combat architecture

The plugin is intentionally combat-only.

```text
Paper input / damage event
           |
           v
  input boundary listeners
           |
      +----+----------------------+
      |                           |
      v                           v
 ComboService              BasicAttackService
      |                           |
      v                           v
 AbilityEngine          WeaponDefinitionResolver
      |                           |
      |                     DamageRequest
      |                           |
      |                           v
      |                   CombatDamageService
      |                           ^
      |                           |
      +------ abilities ----------+
      |
      +------ ArcaneBoltAbility
                 |
                 v
       VirtualProjectileService
                 |
          Paper World.rayTrace
                 |
          +------+------+
          |             |
          v             v
       block hit     entity hit
                        |
                        +----> CombatDamageService
                        |
                        +----> StatusEffectService
                                   |
                                   v
                           StatusEffectTracker
```

## Scheduler model

There is one repeating combat scheduler:

```text
CombatTicker (every server tick)
    |
    +-- VirtualProjectileService.tick()
    |
    +-- StatusEffectService.tick()
    |
    +-- mana regeneration every 10 ticker calls
```

There is no repeating task per:

- player
- cooldown
- combo
- projectile
- status effect

Spell/basic-attack cooldowns and combo expiry remain timestamp based.

## Projectile model

Virtual projectiles store only lightweight runtime state:

- owner UUID
- world UUID
- current position
- normalized direction
- traveled distance
- immutable projectile specification

Each tick advances a projectile by at most its configured speed. The service ray-traces the entire movement segment using Paper's combined block/entity collision query.

A hit terminates the projectile immediately.

The projectile system does not create Minecraft projectile entities, armor stands, displays, or scheduler tasks.

## Arcane Bolt

Arcane Bolt uses a `ProjectileSpec` with:

- speed: 1.35 blocks/tick
- maximum distance: 14 blocks
- collision radius: 0.45 blocks
- magic base damage: 8
- END_ROD trail/impact particles
- refreshable burn on entity impact

Its ability implementation only asks `VirtualProjectileService` to spawn the specification. Collision, lifetime, damage, and on-hit status handling remain outside the ability.

## Status effects

Pure status state is represented by:

- `StatusEffectSpec`
- `ActiveStatusEffect`
- `StatusEffectTracker`
- `StatusEffectTick`

A status specification contains:

- type
- strength
- duration
- tick interval
- stack policy

Supported stack policies:

- `REPLACE`
- `REFRESH`
- `STACK_INTENSITY`

The Paper-facing `StatusEffectService` stores trackers by target entity UUID. It resolves entities/sources only while ticking, so effect state does not retain Bukkit entity references.

Current periodic behaviors:

- `BURN` -> magic damage
- `BLEED` -> physical damage

Periodic damage uses `DamageRequest.withoutCriticals()`, making DoT critical eligibility an explicit rule rather than an accidental consequence of attacker stats.

## Damage pipeline

`DamageRequest` describes the attack itself:

- damage type
- base damage
- additive damage
- attack multiplier
- critical chance bonus
- critical damage multiplier bonus
- whether this damage is eligible to crit

The calculator applies:

1. base + additive damage
2. finite-value validation and safety cap
3. request multiplier
4. attacker physical/magic multiplier
5. optional critical roll and critical multiplier
6. defender defense mitigation
7. final safety cap

Abilities and status effects do not implement their own arithmetic.

## Rules

- Paper listeners translate events and delegate; they do not own combat formulas.
- Pure domain state does not depend on Bukkit/Paper where practical and is unit tested without a server.
- Mana and spell cooldown mutations are centralized.
- Combo expiry is checked when input or melee decisions occur.
- Combat state stores UUID-keyed data and does not retain disconnected player references.
- Vanilla player melee damage is cancelled and replaced by `BasicAttackService`.
- Left-clicking an entity during an active combo is routed into `ComboService`.
- Internal damage application is explicitly marked so plugin damage is not reinterpreted as a fresh basic attack.
- Player stats are immutable values replaced atomically on `PlayerCombatState`.
- Random critical rolls are injected into the calculator, keeping tests deterministic.
- Projectile collision is segment-based to avoid obvious tunneling.
- Status stacking semantics are explicit rather than implicit.
