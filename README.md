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
- spell-generated damage recursion protection
- targeting, knockback, particles, sounds, and action-bar feedback
- `/combat mana`, `/combat stats`, `/combat combo`, and `/combat cast`
- unit tests for the pure combat domain

All spell combos currently begin with right click. Left-clicking an entity while a combo is active contributes the `LEFT` combo input; otherwise it executes the custom basic attack and cancels vanilla melee damage.

## Prototype combos

| Ability | Combo |
| --- | --- |
| Bash | R-L-L |
| Dash | R-R-L |
| Arcane Bolt | R-L-R |
| Blink | R-R-R |

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
