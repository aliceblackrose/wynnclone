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
      +-------------+-------------+
                    |
                    v
             DamageRequest
                    |
                    v
            CombatDamageService
               /          \
              v            v
       CombatStats    DamageCalculator
                          |
                          v
                    DamageResult
                          |
                          v
                    Paper damage
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
- Spell and basic-attack damage share `DamageRequest -> CombatDamageService -> DamageCalculator`.
- Internal damage application is explicitly marked so the melee listener does not recursively reinterpret plugin-generated damage as a fresh player attack.
- Player stats are immutable values replaced atomically on `PlayerCombatState`.
- Random critical rolls are injected into the calculator, keeping tests deterministic.

## Combat stats

`CombatStats` contains:

- physical damage multiplier
- magic damage multiplier
- critical chance
- critical damage multiplier
- defense

The damage type selects the relevant attacker multiplier. Target player defense comes from that target's own combat state. Non-player living entities currently use neutral/default stats until a dedicated mob-stat layer exists.

## Damage pipeline

`DamageRequest` describes the attack itself:

- damage type
- base damage
- additive damage
- attack multiplier
- critical chance bonus
- critical damage multiplier bonus

The calculator applies:

1. base + additive damage
2. finite-value validation and safety cap
3. request multiplier
4. attacker physical/magic multiplier
5. critical roll and critical multiplier
6. defender defense mitigation
7. final safety cap

Abilities only declare their own base damage/type. They do not inspect player stats or implement critical formulas.

## Weapon boundary

`WeaponDefinitionResolver` is deliberately small and Paper-facing. It maps a held vanilla `Material` to an immutable `WeaponDefinition` containing:

- stable weapon ID
- base attack damage
- `AttackSpeed`
- critical chance bonus

The rest of combat consumes the definition and has no material-specific branches. A future custom item/equipment system should replace this resolver rather than spreading item checks through combat code.
