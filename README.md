# WynnClone Combat

A Java 25 / Paper 26.2 combat prototype focused entirely on responsive action-RPG combat.

## Current combat

- ordered left/right click spell combos
- custom melee/basic attacks
- weapon attack-speed gates
- mana and timestamp-based cooldowns
- physical/magic damage scaling
- critical hits and defense mitigation
- virtual projectiles with segment collision
- piercing projectile hit limits
- optional projectile explosions
- burn and bleed status effects
- explicit status stacking policies
- delayed area telegraphs
- shared circular AoE damage/status/knockback
- one central combat ticker
- particles, sounds, action-bar feedback, and debug commands
- pure-domain regression tests

## Prototype combos

| Ability | Combo | Behavior |
| --- | --- | --- |
| Bash | R-L-L | 350 ms warned circular impact with damage + knockback |
| Dash | R-R-L | forward movement |
| Arcane Bolt | R-L-R | moving magic projectile that can pierce two targets and apply burn |
| Blink | R-R-R | short safe teleport |

## Telegraphs

Bash now demonstrates the telegraph system instead of applying its hit immediately.

On cast:

1. a target area is placed two blocks in front of the caster
2. a 16-point warning ring is rendered for 350 ms
3. the impact resolves once
4. targets inside the horizontal radius and vertical bound are damaged
5. configured knockback/status behavior is applied

The telegraph stores owner/world UUIDs and immutable geometry/combat data rather than retaining a `Player` or `World`.

If the caster disconnects, dies, or changes worlds before impact, the telegraph is discarded.

## Area impacts

`AreaImpactRequest` is the shared AoE combat definition used by telegraphs and projectile explosions.

It contains:

- horizontal radius
- vertical radius
- damage request
- optional status effect
- horizontal knockback
- vertical knockback

This keeps AoE arithmetic and target application outside individual abilities.

## Projectile behaviors

A `ProjectileSpec` now includes:

- speed per tick
- maximum travel distance
- collision radius
- damage request
- trail/impact particles
- optional status effect
- maximum distinct entity hits
- optional explosion radius

### Piercing

Non-explosive projectiles can hit more than one entity.

Each active projectile tracks only the UUIDs it has already hit. The ray-trace filter excludes those UUIDs, then the same tick's remaining movement segment continues after the collision.

Arcane Bolt currently has:

- 1.35 blocks/tick
- 14-block maximum distance
- 0.45 collision radius
- 2-target hit limit
- magic damage
- refreshable burn

### Explosions

An explosive projectile stops on its first entity or block collision and resolves a shared area impact at that location.

To keep semantics deterministic, explosive specifications are rejected if they also request more than one direct entity hit.

## Scheduler model

One `CombatTicker` runs once per server tick:

```text
CombatTicker
  ├─ VirtualProjectileService.tick()
  ├─ StatusEffectService.tick()
  ├─ AreaTelegraphService.tick()
  └─ mana regeneration every 10 ticks
```

There is no scheduler task per player, cooldown, combo, projectile, status effect, or telegraph.

## Debug commands

```text
/combat mana
/combat stats
/combat effects
/combat projectiles
/combat telegraphs
/combat combo
/combat cast <ability-id>
```

## Build

Requires Java 25.

```bash
./gradlew clean build --warning-mode all --stacktrace
```

Run a Paper 26.2 development server with:

```bash
./gradlew runServer
```
