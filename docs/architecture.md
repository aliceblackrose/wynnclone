# Combat architecture

The plugin is intentionally combat-only.

```text
Paper input / damage event
           |
           v
  input boundary listeners
           |
      +----+------------------+
      |                       |
      v                       v
 ComboService          BasicAttackService
      |                       |
      v                       v
 AbilityEngine         WeaponProfileResolver
      |                       |
      +-----------+-----------+
                  |
                  v
           DamageRequest
                  |
                  v
          DamageCalculator
                  |
                  v
       CombatDamageService
                  |
          +-------+-------+
          |               |
          v               v
      targeting      Paper effects
```

## Rules

- Paper listeners translate events and delegate; they do not own combat formulas.
- Pure domain state does not depend on Bukkit/Paper and is unit tested without a server.
- Mana and spell cooldown mutations are centralized in the ability engine.
- Spell cooldowns and basic-attack gates use expiry timestamps instead of decrementing counters every tick.
- Combo expiry is checked when input or melee decisions occur; no per-player combo task exists.
- One shared scheduler regenerates mana for all tracked players.
- Combat state stores UUID-keyed data only and never retains disconnected Player objects.
- Ability IDs and combos are unique and validated during registration.
- Vanilla player melee damage is cancelled and replaced by `BasicAttackService`.
- Left-clicking an entity during an active combo is routed into `ComboService` instead of the basic attack.
- Spell and basic-attack damage share `DamageRequest -> DamageCalculator -> CombatDamageService`.
- Internal damage application is explicitly marked so the melee listener does not recursively reinterpret plugin-generated damage as a fresh player attack.

## Damage pipeline

`DamageRequest` currently carries:

- damage type
- base damage
- additive damage
- multiplier
- defense

The calculator applies:

1. base + additive damage
2. finite-value validation and safety cap
3. multiplicative scaling
4. defense mitigation
5. final safety cap

The current abilities use zero defense because a stat/equipment system is intentionally outside this slice. That field is already part of the canonical request so target defense can be wired in later without moving formulas back into abilities.

## Weapon boundary

`WeaponProfileResolver` is deliberately small and Paper-facing. For now it maps vanilla held materials to:

- base attack damage
- `AttackSpeed`

The rest of combat only consumes the resulting `WeaponProfile`. A future RPG item system should replace this mapping rather than adding item checks throughout combat code.
