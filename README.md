# WynnClone Combat

A Java 25 / Paper 26.2 combat prototype focused on fast click-combo abilities and a controlled RPG damage pipeline.

## Current combat

- ordered left/right click combos
- per-player combat state
- mana and centralized regeneration
- timestamp-based spell cooldowns
- custom basic attacks with weapon attack-speed gates
- canonical physical/magic damage requests and calculation
- spell-generated damage recursion protection
- targeting, knockback, particles, and action-bar feedback
- `/combat` debug command
- unit tests for the pure combat domain

All spell combos currently begin with right click. Left-clicking an entity while a combo is active contributes the `LEFT` combo input; otherwise it executes the custom basic attack and cancels vanilla melee damage.

## Prototype combos

| Ability | Combo |
| --- | --- |
| Bash | R-L-L |
| Dash | R-R-L |
| Arcane Bolt | R-L-R |
| Blink | R-R-R |

## Basic attack profiles

The prototype resolves the held vanilla item into a small weapon profile:

- swords: normal attack speed
- axes: slow attack speed
- mace: very slow attack speed
- trident: normal attack speed
- other items / fists: fast attack speed

This is intentionally a boundary implementation. Custom RPG weapon definitions can replace the resolver later without changing the attack or damage pipeline.

## Build

Requires Java 25.

```bash
./gradlew clean build --warning-mode all --stacktrace
```

Run a Paper 26.2 development server with:

```bash
./gradlew runServer
```
