# WynnClone Combat

A Java 25 / Paper 26.2 combat prototype focused on fast click-combo abilities and a controlled RPG damage pipeline.

## Current combat

- ordered left/right click combos
- per-player combat state
- mana and centralized regeneration
- timestamp-based spell cooldowns
- custom basic attacks with weapon attack-speed gates
- immutable combat stats
- physical and magic damage multipliers
- defense mitigation
- deterministic/testable critical-hit calculation
- stable weapon definitions with per-weapon crit bonuses
- centralized virtual projectile simulation
- segment ray-trace collision against blocks and living entities
- explicit status effects with replace/refresh/intensity stacking
- burn and bleed tick behavior
- non-critical damage-over-time requests
- one combat ticker for projectiles, effects, and mana cadence
- spell-generated damage recursion protection
- targeting, knockback, particles, sounds, and action-bar feedback
- `/combat mana`, `/combat stats`, `/combat effects`, `/combat projectiles`, `/combat combo`, and `/combat cast`
- unit tests for the pure combat domain

All spell combos currently begin with right click. Left-clicking an entity while a combo is active contributes the `LEFT` combo input; otherwise it executes the custom basic attack and cancels vanilla melee damage.

## Prototype combos

| Ability | Combo |
| --- | --- |
| Bash | R-L-L |
| Dash | R-R-L |
| Arcane Bolt | R-L-R |
| Blink | R-R-R |

## Arcane Bolt

Arcane Bolt is now a virtual projectile rather than an instant entity ray.

It:

- starts at the caster eye position
- moves 1.35 blocks per server tick
- has a 14-block maximum travel distance
- checks the complete movement segment each tick with Paper ray tracing
- stops on the nearest solid block or eligible living entity
- deals magic damage through the normal combat damage pipeline
- applies a short refreshable burn on entity impact
- renders only particles; it does not spawn a Bukkit projectile entity

Because collision is checked along each segment rather than only at discrete points, targets cannot normally be skipped simply because the projectile moves more than one block per tick.

## Status effects

The prototype status engine currently defines:

- `BURN`: periodic magic damage
- `BLEED`: periodic physical damage

Status definitions explicitly specify:

- strength
- duration
- tick interval
- stack policy

Supported stack policies are:

- `REPLACE`
- `REFRESH`
- `STACK_INTENSITY`

Status effects are stored by entity UUID and processed by the shared combat ticker. There is no scheduler task per effect.

Damage-over-time ticks call the same `CombatDamageService` as normal attacks, but their `DamageRequest` explicitly disables critical hits.

## Combat ticker

One once-per-tick combat scheduler drives:

1. virtual projectile movement and collision
2. status-effect ticking
3. mana regeneration on an internal 10-tick cadence

This avoids creating one scheduler task per player, projectile, effect, or cooldown.

## Combat stats

Every tracked player currently starts with:

- physical damage multiplier: `1.00x`
- magic damage multiplier: `1.00x`
- critical chance: `5%`
- critical damage: `1.50x`
- defense: `0`

These values are held in an immutable `CombatStats` value. Equipment/class systems can replace the runtime stats later without moving formulas into abilities.

Critical hits use an injected random roll inside `DamageCalculator`, so the calculation is deterministic in unit tests. Successful crits produce a crit particle burst and sound in game.

## Weapon definitions

The Paper-facing resolver converts the held vanilla item into a stable `WeaponDefinition` containing:

- stable ID
- base damage
- attack speed
- critical chance bonus

Current prototype behavior:

- swords: normal attack speed, +2% crit chance
- axes: slow attack speed
- mace: very slow attack speed, +5% crit chance
- trident: normal attack speed, +3% crit chance
- other items / fists: fast attack speed

This resolver is intentionally a boundary. A future custom-item system can replace its definitions without changing the attack or damage engine.

## Build

Requires Java 25.

```bash
./gradlew clean build --warning-mode all --stacktrace
```

Run a Paper 26.2 development server with:

```bash
./gradlew runServer
```
