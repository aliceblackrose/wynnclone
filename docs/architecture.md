# Combat architecture

The plugin is intentionally combat-only.

```text
Paper input event
      |
      v
CombatInputListener
      |
      v
ComboService ----> AbilityCatalog
      |
      v
AbilityEngine ----> PlayerCombatState
      |
      v
AbilityAction
      |
      +----> TargetingService
      +----> CombatDamageService
      +----> movement / particles
```

## Rules

- Paper listeners translate events and delegate; they do not own combat rules.
- Pure domain state does not depend on Bukkit/Paper and is unit tested without a server.
- Mana and cooldown mutations are centralized in the ability engine.
- Cooldowns use expiry timestamps instead of decrementing counters every tick.
- Combo expiry is checked only when input arrives; no per-player combo task exists.
- One shared scheduler regenerates mana for all tracked players.
- Combat state stores UUID-keyed data only and never retains disconnected Player objects.
- Ability IDs and combos are unique and validated during registration.

The first slice deliberately leaves vanilla melee damage in place. Once custom weapon/stat damage is introduced, vanilla attack interception should be added as a separate, tested boundary rather than mixed into the combo system.
