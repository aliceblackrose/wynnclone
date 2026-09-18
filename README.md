# WynnClone Combat

A Java 25 / Paper 26.2 combat prototype focused on fast click-combo abilities.

## Current slice

- ordered left/right click combos
- per-player combat state
- mana and regeneration
- cooldowns stored as expiry times
- central ability execution
- Bash, Dash, Arcane Bolt, and Blink prototype abilities
- action-bar combat feedback
- `/combat` debug command
- unit tests for the pure combat domain

All spell combos currently begin with right click so ordinary left-click attacks remain vanilla when no combo is active.

## Prototype combos

| Ability | Combo |
| --- | --- |
| Bash | R-L-L |
| Dash | R-R-L |
| Arcane Bolt | R-L-R |
| Blink | R-R-R |

## Build

Requires Java 25.

```bash
./gradlew clean build --warning-mode all --stacktrace
```

Run a Paper 26.2 development server with:

```bash
./gradlew runServer
```
